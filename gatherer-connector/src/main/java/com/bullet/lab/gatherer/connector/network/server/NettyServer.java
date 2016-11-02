package com.bullet.lab.gatherer.connector.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by pudongxu on 16/11/1.
 */
public abstract class NettyServer implements Server<ChannelInitializer<SocketChannel>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ServerBootstrap bootstrap;
    private final EventLoopGroup boss;
    private final EventLoopGroup worker;
    private int port;

    public NettyServer() {
        bootstrap = new ServerBootstrap();
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
    }

    @Override
    public Channel start() {
        Channel channel = bootstrap.bind(port).syncUninterruptibly().channel();
        logger.debug("{} server start!",getClass());
        return channel;
    }

    @Override
    public void bind(int port, ChannelInitializer<SocketChannel> handler) {
        this.port = port;
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,4096)
                .childHandler(handler);
        logger.debug("netty server bind port {}", port);
    }

    @Override
    public void close() throws IOException {
        if (boss != null)
            boss.shutdownGracefully();
        if (worker != null)
            worker.shutdownGracefully();
    }

    public abstract void initBootstrap();
}
