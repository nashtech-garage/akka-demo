package com.example.persistence;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.persistence.typed.PersistenceId;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaPersistenceMain {

    public static void main( String[] args ) {
        // Create an actor system
        Config config = ConfigFactory.load();
        Behavior<AkkaEventSourcedBehavior.Command> akkaPersistentBehavior = AkkaEventSourcedBehavior.create(PersistenceId.ofUniqueId("event-sourced-actor"));
        ActorSystem<AkkaEventSourcedBehavior.Command> persistentActor = ActorSystem.create(akkaPersistentBehavior, "example-system", config);

        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 1"));
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 2"));
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 3"));
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 4"));
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 5"));
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 6"));
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 7"));
        persistentActor.tell(AkkaEventSourcedBehavior.Clear.INSTANCE);
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 8"));
        persistentActor.tell(new AkkaEventSourcedBehavior.Add("Item 9"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        persistentActor.terminate();
    }

//    public static void main( String[] args ) {
//        // Create an actor system
//        Config config = ConfigFactory.load();
//        Behavior<AkkaDurableStateBehavior.Command> akkaPersistentBehavior = AkkaDurableStateBehavior.create(PersistenceId.ofUniqueId("durable-state-actor"));
//        ActorSystem<AkkaDurableStateBehavior.Command> persistentActor = ActorSystem.create(akkaPersistentBehavior, "example-system", config);
//
//        persistentActor.tell(AkkaDurableStateBehavior.Increment.INSTANCE);
//        persistentActor.tell(new AkkaDurableStateBehavior.IncrementBy(10));
//        persistentActor.tell(new AkkaDurableStateBehavior.IncrementBy(-5));
//        persistentActor.tell(new AkkaDurableStateBehavior.IncrementBy(20));
//        persistentActor.tell(new AkkaDurableStateBehavior.IncrementBy(50));
//        persistentActor.tell(AkkaDurableStateBehavior.Increment.INSTANCE);
//        persistentActor.tell(new AkkaDurableStateBehavior.IncrementBy(-20));
//        persistentActor.tell(new AkkaDurableStateBehavior.IncrementBy(-10));
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        persistentActor.terminate();
//    }
}
