package com.cubic.viedo.store;

import com.cubic.viedo.webscoket.WebConnection;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.Optional;

/**
 * @ClassName WebConnection
 * @Author QIANGLU
 * @Date 2020/4/6 11:09 上午
 * @Version 1.0
 */
public interface WebConnectionStore {


    WebConnection register(String uid, Channel channel);

    public Optional<WebConnection> getConnection(Channel channel);

    public Map<Channel, WebConnection> getAllConnection();


}
