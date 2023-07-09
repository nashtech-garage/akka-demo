/**
 * 
 */
package shopping.order;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

import akka.actor.typed.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;

/**
 * @author loinguyenx
 *
 */
public class OrderServiceHttpServer {

	static void startHTTPServer(Route route, ActorSystem<?> system) {
		CompletionStage<ServerBinding> futureBinding = Http.get(system).newServerAt("localhost", 8080).bind(route);

		futureBinding.whenComplete((binding, exception) -> {
			if (binding != null) {
				InetSocketAddress address = binding.localAddress();
				system.log().info("Server online at http://{}:{}/", address.getHostString(), address.getPort());
			} else {
				system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
				system.terminate();
			}
		});
	}


}
