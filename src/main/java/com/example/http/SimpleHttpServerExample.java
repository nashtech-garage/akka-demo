package com.example.http;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

import java.util.concurrent.CompletionStage;

public class SimpleHttpServerExample extends AllDirectives {

    public static void main(String[] args) throws Exception {
        // boot up server using the route as defined below
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "routes");
        final Http http = Http.get(system);

        // In order to access all directives we need an instance where the routes are define.
        SimpleHttpServerExample app = new SimpleHttpServerExample();

        final CompletionStage<ServerBinding> binding = http.newServerAt("localhost", 8080).bind(app.createRoute());

        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        // trigger unbinding from the port // and shutdown when done
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        return concat(
                path("hello", () -> get(() -> complete("<h1>Say hello to akka-http</h1>")))
        );
    }

}
