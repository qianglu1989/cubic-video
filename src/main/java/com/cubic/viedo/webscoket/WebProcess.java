package com.cubic.viedo.webscoket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * @ClassName WebProcess
 * @Author QIANGLU
 * @Date 2020/4/8 3:47 下午
 * @Version 1.0
 */
public interface WebProcess {

    int code();

    /**
     * 处理视频流
     * @param id
     * @param byteBuf
     * @return
     */
    String process(String id,ByteBuf byteBuf);

    /**
     * 处理业务数据
     * @param channel
     * @param data
     * @return
     */
    String process(Channel channel, String data);

}
