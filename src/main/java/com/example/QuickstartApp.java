package com.example;

import akka.actor.typed.ActorSystem;
import com.example.practices.actors.HelloBehavior;
import com.example.practices.messages.Hello;
import com.example.practices.messages.Message;

//#main-class
public class QuickstartApp {

    public static void main(String[] args) {
        // Initialize Actor System with Hello Behavior
        ActorSystem<Message> system = ActorSystem.create(HelloBehavior.create(), "hello-world");

        // Send message to HelloBehavior
        system.tell(Hello.builder().message("hello-world").build());
    }

}
//#main-class
