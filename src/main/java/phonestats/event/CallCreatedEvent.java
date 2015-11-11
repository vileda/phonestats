package phonestats.event;

import io.resx.core.event.SourcedEvent;
import lombok.Getter;
import lombok.Setter;

import static phonestats.Constants.CALL_CREATED_EVENT_ADDRESS;

@Getter
@Setter
public class CallCreatedEvent extends SourcedEvent {
	private String callId;

	public CallCreatedEvent() {
		super(CALL_CREATED_EVENT_ADDRESS, null);
	}

	public CallCreatedEvent(String id, String callId) {
		super(CALL_CREATED_EVENT_ADDRESS, id);
		this.callId = callId;
	}
}
