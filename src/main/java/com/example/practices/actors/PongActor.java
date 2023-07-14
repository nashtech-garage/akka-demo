package com.example.practices.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.practices.messages.Message;
import com.example.practices.messages.PingCommand;
import com.example.practices.messages.PongMessage;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

@Slf4j
public class PongActor extends AbstractBehavior<Message> {
  private final Duration delay;

  private PongActor(ActorContext<Message> context, Duration delay) {
    super(context);
    this.delay = delay;
  }

  static Behavior<Message> create(Duration delay) {
    return Behaviors.setup(ctx -> new PongActor(ctx, delay));
  }

  static Behavior<Message> create() {
    return Behaviors.setup(ctx -> new PongActor(ctx, null));
  }

  @Override
  public Receive<Message> createReceive() {
    return newReceiveBuilder()
        .onMessage(PingCommand.class, this::onPing)
        .onSignal(PostStop.class, this::onStop)
        .build();
  }

  private Behavior<Message> onPing(PingCommand command) {
    log.info("GOT PING {}", command);
    PongMessage message = PongMessage.builder().requestId(command.getId()).message("PONG").build();
    Optional.ofNullable(delay)
        .ifPresentOrElse(d -> getContext().scheduleOnce(delay, command.responseTo(), message), () -> command.responseTo().tell(message));
    return this;
  }

  private Behavior<Message> onStop(PostStop signal) {
    log.warn("Goodbye.");
    return Behaviors.empty();
  }
}
