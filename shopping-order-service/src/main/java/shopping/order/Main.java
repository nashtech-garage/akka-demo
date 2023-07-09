package shopping.order;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class Main {
	public static void main(String[] args) throws Exception {
		// #server-bootstrapping
		Behavior<NotUsed> rootBehavior = Behaviors.setup(context -> {
			ActorRef<OrderActors.Command> actorRef = context.spawn(OrderActors.create(), "OrderService");
			OrderServiceRoutes orderRoutes = new OrderServiceRoutes(context.getSystem(), actorRef);

			OrderServiceHttpServer.startHTTPServer(orderRoutes.routes(), context.getSystem());

			return Behaviors.empty();
		});

		// boot up server using the route as defined below
		ActorSystem.create(rootBehavior, "OrderServiceHttpServer");
		// #server-bootstrapping
	}

}
