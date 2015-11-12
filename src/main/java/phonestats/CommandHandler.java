package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.json.Json;
import phonestats.aggregate.Dashboard;
import phonestats.command.CreateCallCommand;
import phonestats.event.CallCreatedEvent;

import static phonestats.Constants.UPDATE_DASHBOARD_EVENT_ADDRESS;

public class CommandHandler {
	private EventStore eventStore;

	public CommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
		attachCommandHandlers();
	}

	private void attachCommandHandlers() {
		eventStore.consumer(CreateCallCommand.class, message -> {
			CreateCallCommand createCommand = Json.decodeValue(message.body(), CreateCallCommand.class);
			CallCreatedEvent createdEvent = new CallCreatedEvent(createCommand.getId(), createCommand.getCallId());
			eventStore.publish(createdEvent, CallCreatedEvent.class).subscribe(event -> {
				eventStore.load(event.getId(), Dashboard.class)
						.subscribe(dashboard -> eventStore
								.publish(UPDATE_DASHBOARD_EVENT_ADDRESS, dashboard, createdEvent));
			});
			message.reply(createCommand.getId());
		});
	}
}
