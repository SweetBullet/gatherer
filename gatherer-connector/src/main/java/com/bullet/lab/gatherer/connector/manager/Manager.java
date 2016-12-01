package com.bullet.lab.gatherer.connector.manager;

import com.bullet.lab.gatherer.connector.event.EventContext;

/**
 * Created by pudongxu on 16/12/1.
 */
public interface Manager {

    void processConnect(EventContext ec);

    void processDefault(EventContext ec);

    void processDisconnect(EventContext ec);
}
