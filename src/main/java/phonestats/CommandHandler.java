package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.json.Json;
import phonestats.command.CreateCallCommand;
import phonestats.event.CallCreatedEvent;

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
			eventStore.publish(createdEvent, CallCreatedEvent.class);
			message.reply(createCommand.getId());
		});
	}
}
