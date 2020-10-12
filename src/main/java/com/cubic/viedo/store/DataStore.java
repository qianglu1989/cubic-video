package com.cubic.viedo.store;

import io.netty.channel.Channel;

import java.util.Map;

/**
 * @ClassName WebConnection
 * @Author QIANGLU
 * @Date 2020/4/6 11:09 上午
 * @Version 1.0
 */
public interface DataStore {


    void register(Channel channel, String data);

    String getData(Channel channel);

    Map<Channel, String> getAllDatas();

    void disposeVideo(Channel channel);

    void remove(Channel channel);


}
