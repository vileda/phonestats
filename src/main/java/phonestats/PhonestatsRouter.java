package phonestats;

import io.resx.core.EventStore;
import io.resx.core.MongoEventStore;
import io.resx.core.command.Command;
import io.vertx.core.json.Json;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import phonestats.event.CallCreatedEvent;
import rx.Observable;

import static phonestats.Constants.*;

public class PhonestatsRouter extends AbstractVerticle {
	public void start() {
		EventBus eventBus = vertx.eventBus();
		EventStore eventStore = new MongoEventStore(vertx, eventBus);

		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);
		Router apiRouter = Router.router(vertx);

		SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);

		SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
		BridgeOptions bridgeOptions = new BridgeOptions();
		bridgeOptions
				.addOutboundPermitted(new PermittedOptions().setAddress(CALL_CREATED_EVENT_ADDRESS));
		sockJSHandler.bridge(bridgeOptions);

		sockJSHandler.socketHandler(new WebsocketHandler(eventStore));

		router.route("/socket/*").handler(sockJSHandler);

		router.route().handler(BodyHandler.create());

		StaticHandler staticHandler = StaticHandler.create();
		router.get().pathRegex("^(/|/js/.*)").handler(staticHandler);

		new CommandHandler(eventStore);
		apiRouter.get("/aggregate/dashboard/:id").handler(new DashboardAggregateHandler(eventStore));
		apiRouter.post("/event/:id").handler(new PhoneCallEventHandler(eventStore));

		router.mountSubRouter("/api", apiRouter);

		server.requestHandler(router::accept).listen(8080);
	}

	public static <T extends Command, R> void publishCommand(T payload, EventStore eventStore, RoutingContext routingContext, Class<R> clazz) {
		HttpServerResponse response = routingContext.response();

		eventStore.publish(payload, clazz)
				.onErrorResumeNext(message -> {
					response.setStatusCode(500).end(message.getMessage());
					return Observable.empty();
				})
				.subscribe(reply -> {
					response.setStatusCode(200).end(Json.encode(reply));
				});
	}
}