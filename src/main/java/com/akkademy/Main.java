package com.akkademy;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {
    public static void main(String[] args) {
        // akkademy是当前服务端Actor System名称，client连接时需要用到
        ActorSystem system = ActorSystem.create("akkademy");
        // akkademy-db是当前服务端Actor名称
        system.actorOf(Props.create(AkkademyDb.class), "akkademy-db");
    }
}
