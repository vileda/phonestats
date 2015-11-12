package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSSocket;
import phonestats.aggregate.Dashboard;
import phonestats.command.CreateCallCommand;
import phonestats.event.CallCreatedEvent;
import phonestats.event.UpdateDashboardEvent;

import static phonestats.Constants.UPDATE_DASHBOARD_EVENT_ADDRESS;

public class WebsocketHandler implements Handler<SockJSSocket> {
	private final EventStore eventStore;

	public WebsocketHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(SockJSSocket sockJSSocket) {
		eventStore.consumer(UpdateDashboardEvent.class, message -> {
			final String id = sockJSSocket.webSession().get("id").toString();
			JsonObject dashboardJson = new JsonObject(message.body());
			if(dashboardJson.getString("id").equals(id)) {
				sockJSSocket.write(Buffer.buffer(message.body()));
			}
		});
		System.out.println();
	}


}
