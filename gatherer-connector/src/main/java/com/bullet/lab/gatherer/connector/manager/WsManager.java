package com.bullet.lab.gatherer.connector.manager;

import com.bullet.lab.gatherer.connector.base.Constants;
import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.pojo.MedicalData;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pudongxu on 16/12/1.
 */
public class WsManager implements Manager {

    private final static Logger logger = LoggerFactory.getLogger(WsManager.class);

    private Map<Integer, Channel> connectionKeeper = new ConcurrentSkipListMap<>();

    private AtomicInteger connectionIdGenerator = new AtomicInteger(1);

    @Override
    public void processConnect(EventContext ec) {
        logger.debug("process connect event");
        Channel channel = ec.channel();
        Integer connectionId = connectionIdGenerator.getAndIncrement();
        channel.attr(Constants.IS_VALID_KEY).set(false);
        channel.attr(Constants.CONNECTION_ID_KEY).set(connectionId);
        this.connectionKeeper.put(connectionId, channel);
    }

    @Override
    public void processDefault(EventContext ec) {
        MedicalData data = ec.getData();
        logger.debug("receive data:{}", data);
    }

    @Override
    public void processDisconnect(EventContext ec) {
        logger.debug("process disconnect event");
    }
}
