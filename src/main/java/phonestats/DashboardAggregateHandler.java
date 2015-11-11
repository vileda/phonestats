package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;
import phonestats.aggregate.Dashboard;

public class DashboardAggregateHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public DashboardAggregateHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		eventStore.load(id, Dashboard.class).subscribe(dashboard -> {
			routingContext.response().end(Json.encode(dashboard));
		});
	}
}
