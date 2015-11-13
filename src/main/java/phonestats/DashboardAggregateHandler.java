package phonestats;

import io.resx.core.MongoEventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import phonestats.aggregate.Dashboard;

public class DashboardAggregateHandler implements Handler<RoutingContext> {
	private final MongoEventStore eventStore;

	public DashboardAggregateHandler(MongoEventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		JsonObject query = new JsonObject().put("payload.id", id);
		eventStore.load(query, Dashboard.class).subscribe(dashboard -> {
			routingContext.response().end(Json.encode(dashboard));
		});
	}
}
