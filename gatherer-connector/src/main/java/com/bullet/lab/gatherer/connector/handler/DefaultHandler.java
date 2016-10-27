package com.bullet.lab.gatherer.connector.handler;

import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.dispatcher.Dispatcher;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bullet.lab.gatherer.connector.protocol.MedicalData;

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
public class DefaultHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Dispatcher dispatcher;

    public DefaultHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        logger.debug("receive data:{}", request);

        if (!request.getDecoderResult().isSuccess()) {
            return;
        }
        if (request.getMethod() != HttpMethod.GET) {
            return;
        }

        String uriString = request.getUri();
        URI uri = new URI(uriString);
        String path = uri.getPath();
        logger.debug("request path:{}", path);
        Map<String, String> map = this.getRequest(request);
        logger.debug("the request is:{}", map);

        MedicalData data = new MedicalData(map.get("name"), map.get("phone"));
        dispatcher.dispatch(EventType.receive, new EventContext() {
            @Override
            public Channel getChannel() {
                return ctx.channel();
            }

            @Override
            public MedicalData getData() {
                return data;
            }
        });


        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.wrappedBuffer("success!".getBytes("UTF-8")));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (HttpHeaders.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response);
        logger.debug("write response!");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("connection build");
        dispatcher.dispatch(EventType.connecting, new EventContext() {
            @Override
            public Channel getChannel() {
                return ctx.channel();
            }

            @Override
            public MedicalData getData() {
                return null;
            }
        });
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        dispatcher.dispatch(EventType.disconnected, new EventContext() {
            @Override
            public Channel getChannel() {
                return ctx.channel();
            }

            @Override
            public MedicalData getData() {
                return null;
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
