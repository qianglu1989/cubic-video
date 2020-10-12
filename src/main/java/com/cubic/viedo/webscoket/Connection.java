
package com.cubic.viedo.webscoket;


import com.google.common.util.concurrent.ListenableFuture;

public interface Connection {


    ListenableFuture<String> write(Object message);

    ListenableFuture<Void> closeFuture();

    boolean isActive();

    void close();
}
