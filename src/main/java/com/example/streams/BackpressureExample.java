package com.example.streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

public class BackpressureExample {

    public static void main( String[] args ) {
        // Create an actor system
        ActorSystem system = ActorSystem.create("akka-streams-backpressure-example");

        // Create an actor materializer
        Materializer materializer = Materializer.createMaterializer(system);

        var stream = noBackpressure();
        // var stream = simpleBackpressure();
        // var stream = customBufferSizeBackpressure();
        // var stream = dropOldestData();
        // var stream = dropNewestData();
        // var stream = dropNewData();
        // var stream = dropBufferedData();
        // var stream = tearDown();

        stream.run(materializer);
    }

    /**
     * Data will be processed one-by-one without backpressure.
     * We can see, this scenario effectively means that the Subscriber will pull the elements from the Publisher
     * – this mode of operation is referred to as pull-based back-pressure.
     *
     * For these reasons, Akka Streams automatically fuses components together:
     *   if we connect components with the via and to methods,
     *   Akka Streams will actually run them on the same actor to eliminate these message exchanges.
     *   The direct consequence is that all data processing happens sequentially.
     */
    private static RunnableGraph<NotUsed> noBackpressure() {
        return source().via(flow()).to(verySlowSink());
    }

    /**
     * For this situation, we need async boundaries:
     *   A way to specify which part(s) of the stream will run on one actor, which part(s) on another actor.
     *   Everything to the left of async runs on one actor, everything on the right runs on another actor.
     * In this case,
     *   the source and flow run on the same actor (call this actor 1),
     *   and the slow sink will run on a different actor (say actor 2).
     *
     * Here’s a breakdown of what’s happening in this case:
     *   1. The sink demands an element, which starts the flow + source.
     *   2. In a snap, the sink receives an element, but it takes 1 second to process it,
     *      so it will send a backpressure signal upstream.
     *   3. During that time, the flow will attempt to keep the throughput of the source, and buffer 16 elements internally.
     *   4. Once the flow’s buffer is full, it will stop receiving new elements.
     *   5. Once per second, the sink will continue to receive an element and print it to the console (the second, slow batch).
     *   6. After 8 elements, the flow’s buffer becomes half-empty.
     *      It will then resume the source and print 8 more elements in a burst, until its buffer is full again.
     *   7. The slow sink will keep doing its thing.
     *   8. After 8 more elements, the flow’s buffer becomes half-empty again, which will resume the source.
     */
    private static RunnableGraph<NotUsed> simpleBackpressure() {
        return source().via(flow()).async()
                .to(verySlowSink());
    }

    private static RunnableGraph<NotUsed> customBufferSizeBackpressure() {
        return source().via(flow().buffer(10, OverflowStrategy.backpressure())).async()
                .to(verySlowSink());
    }

    private static RunnableGraph<NotUsed> dropOldestData() {
        return source().via(flow().buffer(10, OverflowStrategy.dropHead())).async()
                .to(verySlowSink());
    }

    private static RunnableGraph<NotUsed> dropNewestData() {
        return source().via(flow().buffer(10, OverflowStrategy.dropTail())).async()
                .to(verySlowSink());
    }

    private static RunnableGraph<NotUsed> dropNewData() {
        return source().via(flow().buffer(10, OverflowStrategy.dropNew())).async()
                .to(verySlowSink());
    }

    private static RunnableGraph<NotUsed> dropBufferedData() {
        return source().via(flow().buffer(10, OverflowStrategy.dropBuffer())).async()
                .to(verySlowSink());
    }

    /**
     * When we want both the throughput to be high and the data to be intact,
     * the only thing we can do in case of a buffer overflow is to throw an exception,
     * which will fail the entire stream
     */
    private static RunnableGraph<NotUsed> tearDown() {
        return source().via(flow().buffer(10, OverflowStrategy.fail())).async()
                .to(verySlowSink());
    }

    private static Source<Integer, NotUsed> source() {
        return Source.range(1, 100);
    }

    private static Flow<Integer, Integer, NotUsed> flow() {
        return Flow.fromFunction(i -> {
            System.out.println("In Flow: " + i);
            return i;
        });
    }

    private static Sink<Integer, CompletionStage<Done>> sink() {
        return Sink.foreach(System.out::println);
    }

    private static Sink<Integer, CompletionStage<Done>> verySlowSink() {
        return Sink.foreach(x -> {
            Thread.sleep(1000);
            System.out.println("Out: " + x);
        });
    }
}
