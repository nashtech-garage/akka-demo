package com.example.streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.japi.pf.PFBuilder;
import akka.stream.ActorAttributes;
import akka.stream.Materializer;
import akka.stream.RestartSettings;
import akka.stream.Supervision;
import akka.stream.javadsl.RestartSource;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletionStage;

public class ErrorHandlingExample {

    public static void main( String[] args ) {
        // Create an actor system
        ActorSystem system = ActorSystem.create("akka-streams-error-handling-example");

        // Create an actor materializer
        Materializer materializer = Materializer.createMaterializer(system);

        // var stream = logging();
        // var stream = gracefulTerminating();
        // var stream = recoverWithRetries();
        // var stream = restartBackoff();
        var stream = supervision();

        stream.run(materializer);
    }

    private static RunnableGraph<NotUsed> logging() {
        return source()
                .log("ErrorLogging!")
                .to(sink());
    }

    private static RunnableGraph<NotUsed> gracefulTerminating() {
        return source()
                .recover(new PFBuilder<Throwable, Integer>()
                        .match(RuntimeException.class, (e) -> Integer.MIN_VALUE)
                        .build())
                .log("GracefullyStopped!")
                .to(sink());
    }

    private static RunnableGraph<NotUsed> recoverWithRetries() {
        return source()
                .recoverWithRetries(3, new PFBuilder<Throwable, Source<Integer, NotUsed>>()
                        .match(RuntimeException.class, (e) -> Source.range(10, 15))
                        .build())
                .log("RecoveredWithRetries!")
                .to(sink());
    }

    private static RunnableGraph<NotUsed> restartBackoff() {
        return restartSource()
                .log("restartBackoff!")
                .to(sink());
    }

    private static RunnableGraph<NotUsed> supervision() {

        final Function<Throwable, Supervision.Directive> decider =
                exc -> {
                    if (exc instanceof RuntimeException) return (Supervision.Directive) Supervision.resume();
                    else return (Supervision.Directive) Supervision.stop();
                };

        return source()
                .log("RecoveredWithRetries!")
                .withAttributes(ActorAttributes.withSupervisionStrategy(decider))
                .to(sink());
    }


    private static Source<Integer, NotUsed> restartSource() {

        RestartSettings settings = RestartSettings.create(
                    Duration.ofSeconds(3), // min backoff, is the initial duration until the underlying stream is restarted
                    Duration.ofSeconds(30), // max backoff, caps the exponential backoff
                    0.2 // adds 20% "noise" to vary the intervals slightly, allows addition of a random delay following backoff calculation
                ).withMaxRestarts(20, Duration.ofMinutes(5)); // limits the amount of restarts to 20 within 5 minutes

        return RestartSource.withBackoff(
                settings,
                () -> {
                    var randomNumber = new Random().nextInt(20);
                    return Source.range(1, 100).map(x -> {
                        if (x == randomNumber) throw new RuntimeException("Invalid Number: " + x);
                        return x;
                    });
                }
        );
    }

    private static Source<Integer, NotUsed> source() {
        return Source.range(1, 100).map(x -> {
            if (x == 5) throw new RuntimeException("Invalid Number: " + x);
//            if (x == 50) throw new Exception("Stop processing!!!"); // for supervision only
            return x;
        });
    }

    private static Sink<Integer, CompletionStage<Done>> sink() {
        return Sink.foreach(System.out::println);
    }

}
