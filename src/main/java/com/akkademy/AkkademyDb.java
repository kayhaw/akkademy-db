package com.akkademy;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.akkademy.messages.SetRequest;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
    protected final Map<String, Object> map = new HashMap<>();

    private AkkademyDb() {
        // 经典Builder设计模式，只有接收SetRequest对象才会处理，否则输出错误日志
        receive(ReceiveBuilder.match(SetRequest.class, message -> {
            log.info("Received set request – key: {} value: {}", message.getKey(), message.getValue());
            map.put(message.getKey(), message.
                    getValue());
        }).matchAny(o -> log.info("received unknown message {}", o)).build());
    }
}
