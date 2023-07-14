package com.example.practices.messages;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.util.Objects;
import java.util.UUID;

@Builder
public class StartPingCommand implements Message {

  @With @Getter
  private final UUID id;

  @With @Getter
  private final Integer times;

  @With @Getter
  private final ActorRef<Message> pingTo;

  public StartPingCommand(UUID id, Integer times, ActorRef<Message> pingTo) {
    this.id = Objects.isNull(id) ? UUID.randomUUID() : id;
    this.pingTo = Objects.requireNonNull(pingTo);
    this.times = Objects.isNull(times) ? 1 : times;
  }

}
