package com.bullet.lab.gatherer.connector.deliver;

import io.netty.channel.Channel;

import java.io.Closeable;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface Deliver<T> extends Closeable{

    Channel start();

    void bind(int port, T channelHandler);
}
