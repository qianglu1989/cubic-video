package com.cubic.viedo.session;

import com.google.common.util.concurrent.SettableFuture;
import com.cubic.viedo.webscoket.WebConnection;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName DefaultSession
 * @Author QIANGLU
 * @Date 2020/4/22 2:58 下午
 * @Version 1.0
 */
@Slf4j
public class DefaultSession implements Session {

    private final String id;

    private final WebConnection webConnection;


    private SettableFuture<State> future = SettableFuture.create();

    public DefaultSession(String id, WebConnection webConnection ) {
        this.id = id;
        this.webConnection = webConnection;
    }

    @Override
    public void writeToAgent(String data) {

    }

    @Override
    public void writeToWeb(String data) {
        if(log.isDebugEnabled()){
            log.debug("DefaultSession will write data to web  ");

        }
        webConnection.write(new TextWebSocketFrame(data));
        log.info("DefaultSession write to web succ data length:{}");
    }

    @Override
    public WebConnection getWebConnection() {
        return webConnection;
    }


    @Override
    public String getId() {
        return id;
    }
}
