package phonestats;

import io.resx.core.Aggregate;
import io.resx.core.MongoEventStore;
import io.vertx.core.json.Json;
import phonestats.aggregate.Dashboard;
import phonestats.command.CreateCallCommand;
import phonestats.event.CallCreatedEvent;

import static phonestats.Constants.UPDATE_DASHBOARD_EVENT_ADDRESS;

public class CommandHandler {
	private MongoEventStore eventStore;

	public CommandHandler(MongoEventStore eventStore) {
		this.eventStore = eventStore;
		attachCommandHandlers();
	}

	private void attachCommandHandlers() {
		eventStore.consumer(CreateCallCommand.class, message -> {
			CreateCallCommand createCommand = Json.decodeValue(message.body(), CreateCallCommand.class);
			CallCreatedEvent createdEvent = new CallCreatedEvent(createCommand.getId(), createCommand.getCallId());
			eventStore.publish(createdEvent, CallCreatedEvent.class).subscribe(event -> {
				String createdEventId = createdEvent.getId();
				if(eventStore.getAggregateCache().containsKey(createdEventId)) {
					Aggregate aggregate = eventStore.getAggregateCache().get(createdEventId);
					aggregate.apply(createdEvent);
					eventStore.publish(UPDATE_DASHBOARD_EVENT_ADDRESS, aggregate);
				}
				else eventStore.load(event.getId(), Dashboard.class)
						.subscribe(dashboard -> eventStore.publish(UPDATE_DASHBOARD_EVENT_ADDRESS, dashboard));
			});
			message.reply(createCommand.getId());
		});
	}
}
