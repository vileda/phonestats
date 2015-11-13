package phonestats;

import io.resx.core.EventStore;
import io.resx.core.MongoEventStore;
import io.resx.core.command.Command;
import io.vertx.core.json.Json;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CookieHandler;
import io.vertx.rxjava.ext.web.handler.SessionHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.rxjava.ext.web.sstore.LocalSessionStore;
import io.vertx.rxjava.ext.web.sstore.SessionStore;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

public class PhonestatsRouter extends AbstractVerticle {
	public void start() {
		EventBus eventBus = vertx.eventBus();
		//EventStore eventStore = new InMemoryEventStore(eventBus);
		MongoEventStore eventStore = new MongoEventStore(vertx, eventBus);

		SessionStore sessionStore = LocalSessionStore.create(vertx);
		SessionHandler sessionHandler = SessionHandler.create(sessionStore);
		sessionHandler.setNagHttps(false);

		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);
		Router apiRouter = Router.router(vertx);

		router.route().handler(BodyHandler.create());
		router.route().handler(CookieHandler.create());
		router.route().handler(sessionHandler);

		SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);

		SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);

		final Map<String, MessageConsumer<String>> consumers = new HashMap<>();
		final WebsocketHandler websocketHandler = new WebsocketHandler(eventStore, consumers);
		sockJSHandler.socketHandler(websocketHandler);

		router.route("/socket*").handler(sockJSHandler);

		router.get("/io/:id").handler(routingContext -> {
			routingContext.session().put("id", routingContext.request().getParam("id"));
			routingContext.response().sendFile("webroot/index.html");
		});

		StaticHandler staticHandler = StaticHandler.create();
		router.get().pathRegex("^(/|/(js|css)/.*)").handler(staticHandler);


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
