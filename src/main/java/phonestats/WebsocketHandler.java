package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSSocket;
import phonestats.event.UpdateDashboardEvent;

import java.util.Map;

public class WebsocketHandler implements Handler<SockJSSocket> {
	private final EventStore eventStore;
	private final Map<String, MessageConsumer<String>> consumers;

	public WebsocketHandler(EventStore eventStore, Map<String, MessageConsumer<String>> websocketConsumers) {
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
			System.out.println("bound WS handler to sessionId " + sessionId);
			consumers.put(sessionId, consumer);
			System.out.println("registered consumers " + consumers.size());
		}

		sockJSSocket.endHandler(aVoid -> {
			final MessageConsumer<String> consumer = consumers.get(sessionId);
			consumer.unregister();
			consumers.remove(sessionId);
			System.out.println("unregistered consumer for sessionId " + sessionId);
		});
	}
}
