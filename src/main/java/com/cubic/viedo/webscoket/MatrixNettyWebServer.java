package com.cubic.viedo.webscoket;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.cubic.viedo.handler.ChannelCloseHandler;
import com.cubic.viedo.handler.ConnectionCounterHandler;
import com.cubic.viedo.utils.OSUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName NettyServer
 * @Author QIANGLU
 * @Date 2020/3/18 5:39 下午
 * @Version 1.0
 */
@Slf4j
public class MatrixNettyWebServer implements NettyServer {


    private static final int DEFAULT_WRITE_LOW_WATER_MARK = 64 * 1024;

    private static final int DEFAULT_WRITE_HIGH_WATER_MARK = 128 * 1024;

    private final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("web-server-boss").build());

    private final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(OSUtil.getAvailableProcessors(), new ThreadFactoryBuilder().setNameFormat("web-server-worker").build());
    private volatile Channel channel;
    private final int port;
    private WebRequestHandler webRequestHandler;
    private Map<String, ChannelHandler> handlers = new HashMap<>();
    private ApplicationEventPublisher applicationEventPublisher;

    public MatrixNettyWebServer(int port, WebRequestHandler webRequestHandler, ApplicationEventPublisher applicationEventPublisher) {
        this.port = port;
        this.webRequestHandler = webRequestHandler;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Override
    public void start() {
        ConnectionCounterHandler connectionCounterHandler = new ConnectionCounterHandler("matrix-video",applicationEventPublisher);
        ServerBootstrap bootstrap = new ServerBootstrap()
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioServerSocketChannel.class)
                .group(BOSS_GROUP, WORKER_GROUP)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pip = ch.pipeline();
                        pip.addLast(new IdleStateHandler(30, 0, 60))
                                .addLast(new ChannelCloseHandler("websocket"))
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(1024 * 1024))
                                .addLast(new WebSocketServerProtocolHandler("/ws",null, false, 65535*5, false, false,10000L))
                                .addLast(new WebSocketFrameAggregator(1024 * 1024 * 1024))
                                .addLast(connectionCounterHandler)
                                .addLast(webRequestHandler);
                    }
                });

        try {
            this.channel = bootstrap.bind(port).sync().channel();
            log.info("netty web server for agent, port {}", port);
        } catch (Exception e) {
            log.error("netty web server for agent start fail", e);
        }
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void stop() {
        try {
            BOSS_GROUP.shutdownGracefully().sync();
            WORKER_GROUP.shutdownGracefully().sync();
            channel.close();
        } catch (InterruptedException e) {
            log.error("agent client close error", e);
        }
    }


    public void addHander(String name, ChannelHandler handler) {
        handlers.put(name, handler);
    }


}
