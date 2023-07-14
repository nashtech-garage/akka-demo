package com.example.streams;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.*;
import akka.stream.javadsl.*;

/**
 * Akka Streams Graph - Hello world!
 */
@SuppressWarnings("unchecked")
public class AkkaStreamsGraphExample {

    public static void main( String[] args ) {

        // Create the ActorSystem and Materializer
        ActorSystem system = ActorSystem.create("akka-streams-graph-example");
        Materializer materializer = Materializer.createMaterializer(system);

        // Define the graph
        RunnableGraph<NotUsed> graph = RunnableGraph.fromGraph(
            GraphDSL.create(builder -> {
                // Create the necessary shapes
                SourceShape<Integer> sourceShape = builder.add(Source.range(1, 10));
                FlowShape<Integer, Integer> flowShape1 = builder.add(Flow.of(Integer.class).map(x -> x * 10));
                FlowShape<Integer, Integer> flowShape2 = builder.add(Flow.of(Integer.class)
                        .map(x -> x + 2)
                        .filter(x -> x % 2 == 0)
                );
                UniformFanOutShape<Integer, Integer> broadcastShape = builder.add(Broadcast.create(2));
                UniformFanInShape<Integer, Integer> mergeShape = builder.add(Merge.create(2));
                SinkShape<Integer> sinkShape = builder.add(Sink.foreach(System.out::println));

                // Define the connections
                builder.from(sourceShape).viaFanOut(broadcastShape).via(flowShape1).toFanIn(mergeShape);
                builder.from(broadcastShape).via(flowShape2).toFanIn(mergeShape);
                builder.from(mergeShape).to(sinkShape);

                // Return the closed shape
                return ClosedShape.getInstance();
            })
        );

        // Run the graph and materialize the result
        graph.run(materializer);
    }
}
