package event.dispatcher;

import event.EventType;
import event.EventContext;
import event.handler.EventHandler;

import java.io.Closeable;

/**
 * Created by pudongxu on 16/10/26.
 */
public interface Dispatcher extends Closeable{
    void register(EventType eventType, EventHandler eventHandler);

    void dispatch(EventType eventType, EventContext eventContext);
}
