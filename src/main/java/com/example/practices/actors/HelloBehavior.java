package com.example.practices.actors;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.practices.messages.Hello;
import com.example.practices.messages.Message;
import com.example.practices.messages.RemoveCmd;
import com.example.practices.messages.SpawnCmd;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class HelloBehavior extends AbstractBehavior<Message> {
  @Override
  public Receive<Message> createReceive() {
    return newReceiveBuilder()
        .onMessage(Hello.class, this::sayHello)
        .onMessage(SpawnCmd.class, this::spawn)
        .onMessage(RemoveCmd.class, this::remove)
        .onSignal(PostStop.class, this::farewell)
        .onSignal(Terminated.class, this::sayGoodbyeTo)
        .onSignal(PreRestart.class, this::whenRestart)
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

  /**
   * Create a Hello Behavior with SupervisorStrategy
   * @param strategy Supervisor Strategy to specify what happen with the Actor
   *
   * @return HelloBehavior
   */
  public static Behavior<Message> create(SupervisorStrategy strategy) {
    return Behaviors.supervise(create()).onFailure(IllegalArgumentException.class, Objects.requireNonNull(strategy));
  }

  private String path() {
    return this.getContext().getSelf().path().toString();
  }

  /**
   * Handle Hello Message
   * @param helloMessage
   * @return Behavior
   */
  private Behavior<Message> sayHello(Hello helloMessage) {
    log.info("HelloBehavior Actor {}, message {}", this.path(), helloMessage);
    return Behaviors.same();
  }

  /**
   * Handle SpawnCmd command to spawn a new child
   * @param command SpawnCmd command
   * @return Behavior
   */
  private Behavior<Message> spawn(SpawnCmd command) {
    ActorRef<?> newChild = this.getContext().spawn(HelloBehavior.create(), command.getName());
    getContext().watch(newChild);
    return Behaviors.same();
  }

  /**
   * Handle RemoveCmd command to remove a child
   * @param command SpawnCmd command
   * @return Behavior
   */
  private Behavior<Message> remove(RemoveCmd command) {
    this.getContext()
        .getChild(command.getName())
        .ifPresent(c -> this.getContext().stop(c));
    return Behaviors.same();
  }

  /**
   * Handle PostStop signal
   * @param signal
   * @return Behavior
   */
  private Behavior<Message> farewell(PostStop signal) {
    log.warn("Goodbye {}", this.path());
    return Behaviors.empty();
  }

  /**
   * Handle PreRestart signal
   * @param signal
   * @return Behavior
   */
  private Behavior<Message> whenRestart(PreRestart signal) {
    log.warn("Restart Event {}", this.path());
    return Behaviors.empty();
  }

  /**
   * Handle Terminated signal from child
   * @param signal
   * @return Behavior
   */
  private Behavior<Message> sayGoodbyeTo(Terminated signal) {
    log.warn("{} say bye to {}.", this.getContext().getSelf().path().name() ,signal.ref().path().name());
    return Behaviors.empty();
  }
}
