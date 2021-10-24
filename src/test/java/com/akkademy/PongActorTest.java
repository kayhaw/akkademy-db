package com.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.junit.Test;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class PongActorTest {
    ActorSystem system = ActorSystem.create();
    ActorRef actorRef = system.actorOf(Props.create(PongActor.class));
    protected final Logger log = LoggerFactory.getLogger(PongActorTest.class);

    @Test
    public void shouldReplyToPingWithPong() throws Exception {
        Future sFuture = Patterns.ask(actorRef, "Ping", 1000);
        // 把Scala版Future转为Java版的
        final CompletionStage<String> cs = FutureConverters.toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        assert (jFuture.get(1000, TimeUnit.MILLISECONDS).equals("Pong"));
    }

    @Test
    public void shouldReplyToUnknownMsgWithFailure() throws Exception {
        Future sFuture = Patterns.ask(actorRef, "KayHaw", 1000);
        final CompletionStage<String> cs = FutureConverters.toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        jFuture.get(1000, TimeUnit.MILLISECONDS);
    }

    public CompletionStage<String> askPong(String message) {
        Future sFuture = Patterns.ask(actorRef, "Ping", 1000);
        return FutureConverters.toJava(sFuture);
    }

    @Test
    public void printToConsole() throws Exception {
        // thenAccept接收Consumer接口，消费消息但是没有返回值
        askPong("Ping").thenAccept(x -> System.out.println("Replied with" + x));
        // thenApply接收Function接口，消费消息并且产生新消息
        askPong("Ping").thenApply(x -> x.charAt(0));
        // 嵌套异步回调
        CompletionStage<CompletionStage<String>> futureFuture = askPong("Ping").thenApply(x -> askPong(x));
        // 嵌套异步回调简化写法
        CompletionStage<String> cs = askPong("Ping").thenCompose(x -> askPong(x));
        Thread.sleep(1000);
    }

    @Test
    public void handleException() {
        askPong("Error Msg").handle((x, t) -> {
            if (t != null) {
                log.error("Error: {}", t);
            }
            return null;
        });

        // 同步处理异常
        CompletionStage<String> cs = askPong("Error Msg").exceptionally(t -> "default");
        // 异步处理异常
        askPong("Error Msg").handle((x, t) -> t == null ? CompletableFuture.completedFuture(x) : askPong("Ping")).thenCompose(x -> x);
        // 调用链
        askPong("Ping").thenCompose(x -> askPong("Ping"+x)).handle(
                (x, t) -> t != null ? "default" : x
        );
        askPong("Ping").thenCombine(askPong("Ping"), (a, b) -> a+b);
    }
}
