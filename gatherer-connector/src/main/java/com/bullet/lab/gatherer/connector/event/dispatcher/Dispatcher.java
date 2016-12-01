package com.bullet.lab.gatherer.connector.event.dispatcher;

import com.bullet.lab.gatherer.connector.event.EventContext;
import com.bullet.lab.gatherer.connector.event.EventType;
import com.bullet.lab.gatherer.connector.event.EventProcessor;

import java.io.Closeable;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface Dispatcher extends Closeable{

    void register(EventType eventType, EventProcessor eventProcessor);

    void dispatch(EventType eventType, EventContext eventContext);
}
