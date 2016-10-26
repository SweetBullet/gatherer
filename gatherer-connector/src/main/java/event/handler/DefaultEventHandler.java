package event.handler;

import event.EventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by pudongxu on 16/10/26.
 */
public class DefaultEventHandler implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    public void handle(EventContext eventContext) {
    }
}
