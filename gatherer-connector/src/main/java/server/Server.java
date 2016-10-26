package server;

import io.netty.channel.Channel;

import java.io.Closeable;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface Server<T> extends Closeable {
    Channel start();

    void bind(int port, T handler);
}
