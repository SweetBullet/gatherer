package event.handler;

import event.EventContext;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface EventHandler {
    void handle(EventContext eventContext);
}
