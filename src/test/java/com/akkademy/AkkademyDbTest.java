package com.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.akkademy.messages.SetRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AkkademyDbTest {
    // 首先需要创建Actor System
    ActorSystem system = ActorSystem.create();

    @Test
    public void itShouldPlaceKeyValueFromSetMessageIntoMap() {
        TestActorRef<AkkademyDb> actorRef = TestActorRef.create(system, Props.create(AkkademyDb.class));
        // 打印actor地址
        System.out.println(actorRef.path());
        actorRef.tell(new SetRequest("key", "value"), ActorRef.noSender());
        AkkademyDb akkademyDb = actorRef.underlyingActor();
        assertEquals(akkademyDb.map.get("key"), "value");
    }

    @Test
    public void getLastKey() {
        TestActorRef<StateActor> actorRef = TestActorRef.create(system, Props.create(StateActor.class));
        actorRef.tell(new SetRequest("hello", "world"), ActorRef.noSender());
        actorRef.tell(new SetRequest("good", "bye"), ActorRef.noSender());
        StateActor stateActor = actorRef.underlyingActor();
        assertEquals(stateActor.lastStr, "hello");
    }
}
