package com.bullet.lab.gatherer.connector.handler;

import com.bullet.lab.gatherer.connector.base.RequestType;
import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.dispatcher.Dispatcher;
import com.bullet.lab.gatherer.connector.manager.HttpRequestParam;
import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Created by pudongxu on 16/10/27.
 */

@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    private final Dispatcher dispatcher;

    public HttpHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        logger.debug("receive data:{}", request);
        if (!request.getDecoderResult().isSuccess())
            return;
        String uriString = request.getUri();
        URI uri = new URI(uriString);
        String path = uri.getPath();
        logger.debug("request path:{}", path);
        Map<String, String> map = this.getRequest(request);
        logger.debug("the request is:{}", map);
        dispatcher.dispatch(EventType.receive, new EventContext() {
            @Override
            public Channel channel() {
                return ctx.channel();
            }

            @Override
            public RequestType reqType() {
                return RequestType.valueOf(map.get("reqType"));
            }

            @Override
            public HttpRequestParam httpParam() {
                return new HttpRequestParam(request.getMethod().name(), path, "", map);
            }

        });

        //response
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
//                HttpResponseStatus.OK, Unpooled.wrappedBuffer("success!".getBytes("UTF-8")));
//        response.headers().set(CONTENT_TYPE, "text/plain");
//        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
//        if (HttpHeaders.isKeepAlive(request)) {
//            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//        }
//        ctx.writeAndFlush(response);
//        logger.debug("write response!");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("connection build");
        dispatcher.dispatch(EventType.connecting, new EventContext() {
            @Override
            public Channel channel() {
                return ctx.channel();
            }
        });
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        dispatcher.dispatch(EventType.disconnected, new EventContext() {
            @Override
            public Channel channel() {
                return ctx.channel();
            }

        });
    }


    private Map<String, String> getRequest(HttpRequest request) {
        QueryStringDecoder getDecoder = new QueryStringDecoder(request.getUri());
        Map<String, List<String>> params = getDecoder.parameters();
        Map<String, String> map = new HashMap<>();
        params.forEach((paramName, paramValue) -> {
            map.put(paramName, paramValue.get(0));
        });
        return map;
    }
}
