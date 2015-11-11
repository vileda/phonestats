package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import phonestats.command.CreateCallCommand;

public class PhoneCallEventHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public PhoneCallEventHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		CreateCallCommand call = new CreateCallCommand();
		String callId = routingContext.request().getParam("callId");
		call.setCallId(callId);
		PhonestatsVerticle.publishCommand(call, eventStore, routingContext, String.class);
	}
}
