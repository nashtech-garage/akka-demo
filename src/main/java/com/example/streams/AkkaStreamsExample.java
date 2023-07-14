package com.example.streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

/**
 * Akka Streams - Hello world!
 */
public class AkkaStreamsExample {

    public static void main( String[] args ) {
        // Create an actor system
        ActorSystem system = ActorSystem.create("akka-streams-example");

        // Create an actor materializer
        Materializer materializer = Materializer.createMaterializer(system);

        // Create a source that emits numbers from 1 to 10
        Source<Integer, NotUsed> source = Source.range(1, 10);

        // Create a flow that multiplies each element by 2
        Flow<Integer, Integer, NotUsed> flow = Flow.fromFunction(i -> i * 2);

        // Create a sink that prints each element
        Sink<Integer, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        // Connect the source to the flow, then the flow to the sink and run the stream
        var stream = source.via(flow).to(sink);
        stream.run(materializer);
    }
}
