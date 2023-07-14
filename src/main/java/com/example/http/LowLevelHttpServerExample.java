package com.example.http;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.util.ByteString;

import java.util.concurrent.CompletionStage;

public class LowLevelHttpServerExample {

    public static void main(String[] args) throws Exception {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "akka-http-server-low-level");
        final Http http = Http.get(system);
        try {
            CompletionStage<ServerBinding> serverBindingFuture =
                http.newServerAt("localhost", 8080).bindSync(
                    request -> {
                        if (request.getUri().path().equals("/"))
                            return HttpResponse.create().withEntity(ContentTypes.TEXT_HTML_UTF8,
                                    ByteString.fromString("<html><body>Hello world!</body></html>"));
                        else if (request.getUri().path().equals("/ping"))
                            return HttpResponse.create().withEntity(ByteString.fromString("PONG!"));
                        else if (request.getUri().path().equals("/crash"))
                            throw new RuntimeException("BOOM!");
                        else {
                            request.discardEntityBytes(system);
                            return HttpResponse.create().withStatus(StatusCodes.NOT_FOUND).withEntity("Unknown resource!");
                        }
                    });

            serverBindingFuture.whenComplete((binding, exception) -> {
                if (binding != null) {
                    System.out.println("Server is now listening on " + binding.localAddress());
                } else {
                    System.out.println("Server could not start: " + exception.getMessage());
                    system.terminate();
                }});

            System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
            System.in.read(); // let it run until user presses return

            // trigger unbinding from the port and shutdown when done
            serverBindingFuture.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
        } catch (RuntimeException e) {
            system.terminate();
        }
    }
}
