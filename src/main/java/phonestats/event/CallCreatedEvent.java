package phonestats.event;

import io.resx.core.event.SourcedEvent;

import static phonestats.Constants.CALL_CREATED_EVENT_ADDRESS;

public class CallCreatedEvent extends SourcedEvent {
	public CallCreatedEvent(String id) {
		super(CALL_CREATED_EVENT_ADDRESS, id);
	}
}
