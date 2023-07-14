package com.example.persistence;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.state.javadsl.CommandHandler;
import akka.persistence.typed.state.javadsl.DurableStateBehavior;

public class AkkaDurableStateBehavior extends DurableStateBehavior<
        AkkaDurableStateBehavior.Command,
        AkkaDurableStateBehavior.State> {

    private final ActorContext<Command> context;

    interface Command extends CborSerializable {}

    public enum Increment implements Command {
        INSTANCE
    }

    public static class IncrementBy implements Command {
        public final int value;

        public IncrementBy(int value) {
            this.value = value;
        }
    }

    public static class State implements CborSerializable {
        private final int value;

        public State(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }
    }

    public static Behavior<Command> create(PersistenceId persistenceId) {
        return Behaviors.setup(context -> new AkkaDurableStateBehavior(persistenceId, context));
    }

    private AkkaDurableStateBehavior(PersistenceId persistenceId, ActorContext<Command> context) {
        super(persistenceId);
        this.context = context;
    }

    @Override
    public State emptyState() {
        return new State(0);
    }

    @Override
    public CommandHandler<Command, State> commandHandler() {
        return newCommandHandlerBuilder()
                .forAnyState()
                .onCommand(Increment.class,
                        (state, command) -> Effect().persist(new State(state.get() + 1))
                                .thenRun((persistedState) -> context.getLog().info("In command handler")))
                .onCommand(IncrementBy.class,
                        (state, command) -> Effect().persist(new State(state.get() + command.value)))
                .build();
    }
}
