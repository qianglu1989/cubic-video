package com.cubic.viedo.webscoket;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luqiang
 */
public abstract class AbstractConnection implements Connection {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConnection.class);

    private final String name;
    private final Channel channel;

    private final SettableFuture<Void> closeFuture = SettableFuture.create();

    public AbstractConnection(String name, Channel channel) {
        this.name = name;
        this.channel = channel;
    }

    public void init() {
        channel.closeFuture().addListener((f) -> closeFuture.set(null));
    }

    @Override
    public ListenableFuture<String> write(Object message) {
        SettableFuture<String> result = SettableFuture.create();
        if (channel.isWritable()) {
            channel.writeAndFlush(message).addListener(future -> {
                if (future.isSuccess()) {
                    result.set("sucess");
                } else {
                    logger.warn("{} connection write fail, {}, {}", name, channel, message);
                    result.set("fail");
                }
            });
        } else {
            logger.warn("{} connection is not writable, {}, {}", name, channel, message);
            result.set("fail");
        }

        return result;
    }

    @Override
    public ListenableFuture<Void> closeFuture() {
        return closeFuture;
    }

    @Override
    public void close() {
        logger.info("close {} channel {}", name, channel);
        channel.close();
    }
}
