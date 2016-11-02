package com.bullet.lab.gatherer.connector.deliver;

import com.bullet.lab.gatherer.connector.network.codec.GDecoder;
import com.bullet.lab.gatherer.connector.network.codec.GEncoder;
import com.bullet.lab.gatherer.connector.network.server.HttpServer;
import com.bullet.lab.gatherer.connector.network.server.Server;
import com.bullet.lab.gatherer.connector.network.server.WsServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by pudongxu on 16/11/1.
 */
public class WsDeliver implements Deliver<ChannelHandler> {

    private final static Logger logger = LoggerFactory.getLogger(WsDeliver.class);

    private final Server<ChannelInitializer<SocketChannel>> server;

    private Channel channel;

    public WsDeliver() {
        server = new WsServer();
    }

    @Override
    public Channel start() {
        channel = server.start();
        return channel;
    }

    @Override
    public void bind(int port, ChannelHandler channelHandler) {
        server.bind(port, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast("httpDecoder",new HttpResponseEncoder());
                p.addLast("httpEncoder",new HttpRequestDecoder());
                p.addLast("readIdlstate", new IdleStateHandler(20, 0, 0));//读超时
                p.addLast("http-aggretator", new HttpObjectAggregator(1024 * 64));
                p.addLast("websocket-protocal", new WebSocketServerProtocolHandler("/websocket", null, true));
                p.addLast("websocket-handler",channelHandler);
                p.addLast("http-chuncked", new ChunkedWriteHandler());//用于支持处理大数据流

            }
        });
    }

    @Override
    public void close() throws IOException {

    }
}
