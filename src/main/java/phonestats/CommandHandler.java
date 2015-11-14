package phonestats;

import io.resx.core.MongoEventStore;
import io.vertx.core.json.Json;
import phonestats.aggregate.Dashboard;
import phonestats.command.CreateCallCommand;
import phonestats.event.CallCreatedEvent;
import phonestats.event.UpdateDashboardEvent;

import static phonestats.Constants.getTodaysEventsQueryFor;

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
			eventStore.publish(createdEvent, CallCreatedEvent.class)
					.subscribe(event -> {
						String id = event.getId();
						eventStore.load(getTodaysEventsQueryFor(id), Dashboard.class, createdEvent)
								.subscribe(dashboard -> eventStore
										.publish(new UpdateDashboardEvent(), dashboard, getTodaysEventsQueryFor(id).encode())
										.subscribe(dashboard1 -> message.reply(createCommand.getId())));
					});
		});
	}
}
