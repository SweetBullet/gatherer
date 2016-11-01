package com.bullet.lab.gatherer.connector.network;


import io.netty.channel.ChannelFuture;

import java.io.Closeable;

/**
 * Created by pudongxu on 16/11/1.
 */
public interface Connection extends Closeable{

    void init();

    ChannelFuture write();

}
