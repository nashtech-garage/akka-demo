package com.example.practices.messages;

import akka.actor.typed.ActorRef;

/**
 * Command with response
 */
public interface Command extends Message {
  ActorRef<Result> responseTo();
}
