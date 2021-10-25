package com.akkademy;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.akkademy.messages.GetRequest;
import com.akkademy.messages.KeyNotFoundException;
import com.akkademy.messages.SetRequest;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
    protected final Map<String, Object> map = new HashMap<>();

    private AkkademyDb() {
        receive(
            // 匹配Set消息
            ReceiveBuilder.match(SetRequest.class, msg -> {
                log.info("Received Set request: {}", msg);
                map.put(msg.key, msg.value);
                sender().tell(new Status.Success(msg.key), self());
            })
            // 匹配Get消息
            .match(GetRequest.class, msg -> {
                log.info("Received Get request: {}", msg);
                Object value = map.get(msg.key);
                Object response = (value != null) ? value : new Status.Failure(new KeyNotFoundException(msg.key));
                sender().tell(response, self());
                sender().tell(new Status.Success(msg.key), self());
            })
            // 消息不匹配
            .matchAny(o -> {
                sender().tell(new Status.Failure(new ClassNotFoundException()), self());
            })
            .build());
    }
}
