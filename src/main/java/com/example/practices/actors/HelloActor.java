package com.example.practices.actors;

import akka.actor.AbstractActor;
import com.example.practices.messages.Hello;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloActor extends AbstractActor {

  record StartMessage() {
  }

  @Override
  public Receive createReceive() {
    return this.receiveBuilder().match(StartMessage.class, this::startProcessing).build();
  }

  private void startProcessing(StartMessage message) {
    log.info("Start Processing..");
    getContext().become(receiveBuilder().match(Hello.class, this::sayHello).build());
  }

  private void sayHello(Hello message) {
    log.info("Hi, {}", message);

    getSender().tell(message.withMessage("Hi there"), getSelf());
  }

}
