package com.example.streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.Timeout;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class AkkaStreamsActorExample {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("example-system");
        Materializer materializer = Materializer.createMaterializer(system);

        // Create an instance of the actor
        ActorRef actorRef = system.actorOf(Props.create(MyActor.class));

        // Create a source that emits some messages
        Source<String, NotUsed> source = Source.from(List.of("Message 1", "Message 2", "Message 3"));

        Timeout askTimeout = Timeout.apply(5, TimeUnit.SECONDS);

        // Create a sink that sends messages to the actor and processes the responses
        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        // Connect the source to the sink and run the stream
        source
                .ask(3, actorRef, String.class, askTimeout)
                .runWith(sink, materializer);
    }
}

class MyActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    System.out.println("MyActor Received message: " + message);
                    // Perform some processing
                    // Send a response back
                    getSender().tell("Processed " + message, getSelf());
                })
                .build();
    }
}
