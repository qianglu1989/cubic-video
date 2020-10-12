
package com.cubic.viedo.webscoket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cubic.viedo.session.SessionManager;
import com.google.common.collect.ImmutableMap;
import com.cubic.viedo.constant.CommandCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author luqiang
 */
@ChannelHandler.Sharable
@Slf4j
@Service
public class WebRequestHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Resource
    private SessionManager sessionManager;

    private final Map<Integer, WebProcess> processCache;

    public WebRequestHandler(List<WebProcess> webProcesses) {
        ImmutableMap.Builder<Integer, WebProcess> builder = new ImmutableMap.Builder();
        for (WebProcess processor : webProcesses) {
            builder.put(processor.code(), processor);
        }
        processCache = builder.build();

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {


        if (frame instanceof BinaryWebSocketFrame) {
            processCache.get(CommandCode.VIDEO.getCode()).process(ctx.channel().id().asLongText(), frame.content());

        } else if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame tf = (TextWebSocketFrame) frame;
            String data = tf.text();
            JSONObject params = JSON.parseObject(data);
            Integer type = params.getInteger("type");
            String uid = params.getString("uid");
            if (type.intValue() == CommandCode.HEART.getCode()) {
                log.debug("receive heart uid:{}", uid);
                return;
            }

            String appdata = params.getString("data");
            processCache.get(type.intValue()).process(ctx.channel(), appdata);

        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("服务器发生了异常:", cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String requestUri = handshakeComplete.requestUri();
            String subproTocol = handshakeComplete.selectedSubprotocol();
            log.info("web socket ctx {},requestUri:{},subproTocol:{}握手成功。", ctx, requestUri, subproTocol);
            ctx.writeAndFlush(WebResponses.initResponse());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    private void cancelRequest(WebConnection webConnection) {
        webConnection.close();
    }

}
