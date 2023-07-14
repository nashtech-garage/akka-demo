package com.example.practices.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.practices.messages.Hello;
import com.example.practices.messages.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class HelloBehavior extends AbstractBehavior<Message> {
  @Override
  public Receive<Message> createReceive() {
    return newReceiveBuilder()
        .onMessage(Hello.class, this::sayHello)
        .build();
  }

  private HelloBehavior(ActorContext<Message> context) {
    super(context);
    log.info("New Actor is spawned: {}", this.getContext().getSelf().path());
  }

  /**
   * Create a Hello Behavior
   * @return HelloBehavior
   */
  public static Behavior<Message> create() {
    return Behaviors.setup(HelloBehavior::new);
  }

  private String path() {
    return this.getContext().getSelf().path().toString();
  }

  private Behavior<Message> sayHello(Hello helloMessage) {
    log.info("HelloBehavior Actor {}, message {}", this.path(), helloMessage);
    return Behaviors.same();
  }

}
