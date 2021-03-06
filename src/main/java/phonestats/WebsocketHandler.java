package phonestats;

import io.resx.core.MongoEventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSSocket;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import phonestats.event.UpdateDashboardEvent;

import java.util.Map;

@Log4j2
public class WebsocketHandler implements Handler<SockJSSocket> {
	private final MongoEventStore eventStore;
	private final Map<String, MessageConsumer<String>> consumers;

	public WebsocketHandler(MongoEventStore eventStore, Map<String, MessageConsumer<String>> websocketConsumers) {
		this.eventStore = eventStore;
		this.consumers = websocketConsumers;
	}

	@Override
	public void handle(SockJSSocket sockJSSocket) {
		final String sessionId = sockJSSocket.webSession().id();
		final MessageConsumer<String> sessionConsumer = consumers.get(sessionId);

		if(sessionConsumer == null) {
			final MessageConsumer<String> consumer = eventStore.consumer(UpdateDashboardEvent.class, message -> {
				final String id = sockJSSocket.webSession().get("id").toString();
				JsonObject dashboardJson = new JsonObject(message.body());
				if (dashboardJson.getString("id").equals(id))
				{
					sockJSSocket.write(Buffer.buffer(message.body()));
				}
			});
			log.info("bound WS handler to sessionId {}", sessionId);
			consumers.put(sessionId, consumer);
			log.info("registered consumers {}", consumers.size());
		}

		sockJSSocket.endHandler(aVoid -> {
			final MessageConsumer<String> consumer = consumers.get(sessionId);
			consumer.unregister();
			consumers.remove(sessionId);
			log.info("unregistered consumer for sessionId {}", sessionId);
		});
	}
}
