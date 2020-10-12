package com.cubic.viedo.event;

import io.netty.channel.Channel;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName FileDisposeEvent
 * @Author QIANGLU
 * @Date 2020/8/3 2:14 下午
 * @Version 1.0
 */
public class FileDisposeEvent extends ApplicationEvent {

    private Channel channel;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public FileDisposeEvent(Object source, Channel channel) {
        super(source);
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
