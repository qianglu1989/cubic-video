package com.cubic.viedo.webscoket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * @ClassName WebProcess
 * @Author QIANGLU
 * @Date 2020/4/8 3:47 下午
 * @Version 1.0
 */
public abstract class AbstractWebProcess implements WebProcess {

    @Override
    public int code() {
        return 0;
    }

    @Override
    public String process(String id, ByteBuf byteBuf) {


        return null;
    }

    @Override
    public String process(Channel channel, String data) {
        return null;
    }
}
