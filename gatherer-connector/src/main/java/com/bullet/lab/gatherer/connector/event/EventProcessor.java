package com.bullet.lab.gatherer.connector.event;


/**
 * Created by pudongxu on 16/10/26.
 */
public interface EventProcessor {
    void process(EventContext eventContext);
}
