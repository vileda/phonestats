package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import phonestats.command.CreateCallCommand;

import java.util.UUID;

public class PhoneCallEventHandler implements Handler<RoutingContext> {
	private final EventStore eventStore;

	public PhoneCallEventHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(RoutingContext routingContext) {
		CreateCallCommand call = parseCallEvent(routingContext);
		PhonestatsRouter.publishCommand(call, eventStore, routingContext, String.class);
	}

	private CreateCallCommand parseCallEvent(RoutingContext routingContext) {
		CreateCallCommand call = new CreateCallCommand();
		String callId = routingContext.request().getFormAttribute("callId");
		String id = routingContext.request().getParam("id");
		call.setCallId(callId);
		call.setId(id);
		return call;
	}
}
