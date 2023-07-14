package com.example.practices.messages;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.util.UUID;

@Builder
public class PingCommand implements Command {

  @With
  @Getter
  private final UUID id;

  @With
  @Getter
  private final String message;

  @With
  private final ActorRef<Result> responseTo;

  @Override
  public ActorRef<Result> responseTo() {
    return responseTo;
  }

  @Override
  public String toString() {
    return "PingCommand{" +
        "id=" + id +
        ", message='" + message + '\'' +
        '}';
  }
}
