package com.cubic.viedo.webscoket;


import io.netty.channel.Channel;

/**
 * websocket connection
 * @ClassName WebConnection
 * @Author QIANGLU
 * @Date 2020/4/6 11:09 上午
 * @Version 1.0
 */
public interface WebConnection extends Connection {

    Channel getChannel();

    String getUid();

}
