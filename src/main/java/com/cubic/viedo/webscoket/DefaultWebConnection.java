package com.cubic.viedo.webscoket;


import io.netty.channel.Channel;

import java.util.Objects;

/**
 * @ClassName DefaultWebConnection
 * @Author QIANGLU
 * @Date 2020/4/6 11:15 上午
 * @Version 1.0
 */
public class DefaultWebConnection extends AbstractConnection implements WebConnection {

    private final Channel channel;
    private final String uid;

    public DefaultWebConnection(String uid,Channel channel) {
        super("web", channel);
        this.channel = channel;
        this.uid = uid;
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultWebConnection that = (DefaultWebConnection) o;
        return
                Objects.equals(uid, that.uid) &&
                        Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, channel);
    }
}
