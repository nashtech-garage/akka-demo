package com.example.persistence;

import akka.actor.typed.Behavior;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AkkaEventSourcedBehavior extends EventSourcedBehavior<
        AkkaEventSourcedBehavior.Command,
        AkkaEventSourcedBehavior.Event,
        AkkaEventSourcedBehavior.State> {

    /** Command */
    interface Command extends CborSerializable {}

    public static class Add implements Command {
        public final String data;

        public Add(String data) {
            this.data = data;
        }
    }

    public enum Clear implements Command {
        INSTANCE
    }

    /** Event */
    interface Event extends CborSerializable {}

    public static class Added implements Event {
        public final String data;

        public Added(String data) {
            this.data = data;
        }
    }

    public enum Cleared implements Event {
        INSTANCE
    }

    /** State is a List containing the 5 latest items. */
    public static class State implements CborSerializable {
        private final List<String> items;

        private State(List<String> items) {
            this.items = items;
        }

        public State() {
            System.out.println("Initialized a new State!!!");
            this.items = new ArrayList<>();
        }

        public State addItem(String data) {
            List<String> newItems = new ArrayList<>(items);
            newItems.add(0, data);
            // keep 5 items
            List<String> latest = newItems.subList(0, Math.min(5, newItems.size()));
            System.out.println("State items: " + Arrays.toString(latest.toArray()));
            return new State(latest);
        }
    }

    /** Constructor */
    public AkkaEventSourcedBehavior(PersistenceId persistenceId) {
        super(persistenceId);
    }

    public static Behavior<Command> create(PersistenceId persistenceId) {
        return new AkkaEventSourcedBehavior(persistenceId);
    }

    @Override
    public State emptyState() {
        return new State();
    }

    /**
     * The command handler persists the Add payload in an Added event.
     */
    @Override
    public CommandHandler<Command, Event, State> commandHandler() {
        return newCommandHandlerBuilder()
                .forAnyState()
                .onCommand(Add.class, command -> Effect().persist(new Added(command.data)))
                .onCommand(Clear.class, command -> Effect().persist(Cleared.INSTANCE))
                .build();
    }

    /**
     * The event handler appends the item to the state and keeps 5 items.
     *  This is called after successfully persisting the event in the database.
     */
    @Override
    public EventHandler<State, Event> eventHandler() {
        return newEventHandlerBuilder()
                .forAnyState()
                .onEvent(Added.class, (state, event) -> state.addItem(event.data))
                .onEvent(Cleared.class, () -> new State())
                .build();
    }
}
