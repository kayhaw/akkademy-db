package com.akkademy;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.akkademy.messages.SetRequest;

public class StateActor extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
    protected String lastStr;
    protected String curStr;

    StateActor() {
        receive(ReceiveBuilder.match(SetRequest.class, msg -> {
            log.info("Last key is {}", lastStr);
            lastStr = curStr;
            curStr = msg.key;
            log.info("Current key {}", lastStr);
        }).matchAny(o -> log.info("received unknown message {}", o)).build());
    }
}
