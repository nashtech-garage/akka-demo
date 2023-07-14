package com.example.practices.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.practices.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class PingActor extends AbstractBehavior<Message> {
  private final Integer timeout;
  private ActorRef<Message> pingTo = null;
  private Integer maxPing = 1;

  private Integer count = 0;


  private synchronized void set(ActorRef<Message> pingTo, Integer maxPing) {
    this.pingTo = pingTo;
    this.maxPing = Objects.requireNonNull(maxPing);
    if (this.maxPing <= 0) this.maxPing = 1;
  }

  private synchronized void increase() {
    count++;
  }

  private synchronized boolean canContinue() {
    return count < maxPing;
  }

  private synchronized void reset() {
    count = 0;
  }

  private PingActor(ActorContext<Message> context, Integer timeout) {
    super(context);
    this.timeout = Objects.requireNonNull(timeout);
    if (timeout <= 0) throw new IllegalArgumentException("Illegal Timeout value:" + timeout);
  }

  public static Behavior<Message> create(Integer timeout) {
    return Behaviors.setup(ctx -> new PingActor(ctx, timeout));
  }

  public static Behavior<Message> create() {
    return create(1000);
  }

  @Override
  public Receive<Message> createReceive() {
    return newReceiveBuilder()
        .onMessage(StartPingCommand.class, this::onStartPinging)
        .onMessage(PongMessage.class, this::onResponse)
        .onMessage(FailureResult.class, this::onError)
        .onSignal(PostStop.class, this::onStop)
        .build();
  }

  private Behavior<Message> onResponse(PongMessage message) {
    log.info("PONG {}", message);

    if (canContinue()) {
      this.ping();
    }

    return this;
  }

  private Behavior<Message> onStartPinging(StartPingCommand message) {
    reset();
    if (Objects.nonNull(message.getTimes()) && message.getTimes() > 0) {
      log.info("START Pinging");
      this.set(message.getPingTo(), message.getTimes());
      this.ping();
    } else {
      this.ping(message.getPingTo());
    }
    return this;
  }

  private Behavior<Message> onError(FailureResult message) {
    log.error(ExceptionUtils.getRootCauseMessage(message.error()));
    return this;
  }

  private Behavior<Message> onStop(PostStop signal) {
    log.warn("Goodbye.");
    return Behaviors.empty();
  }

  public void ping(ActorRef<Message> resultActorRef) {

    UUID pingRequest = UUID.randomUUID();

    getContext().ask(
        Result.class,
        resultActorRef,
        Duration.ofMillis(timeout),
        (ActorRef<Result> ref) -> PingCommand.builder().id(pingRequest).message("PING").responseTo(ref).build(),
        (res, e) -> {
          if (Objects.nonNull(res)) {
            return res;
          } else if ( e instanceof RuntimeException){
            throw (RuntimeException)e;
          } else {
            throw new IllegalCallerException(e);
          }
        }
    );
  }

  public void ping() {
    Optional.ofNullable(this.pingTo).ifPresent(this::ping);
    increase();
  }
}
