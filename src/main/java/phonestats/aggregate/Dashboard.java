package phonestats.aggregate;

import io.resx.core.Aggregate;
import lombok.Getter;
import lombok.Setter;
import phonestats.event.CallCreatedEvent;

@Getter
@Setter
public class Dashboard extends Aggregate {
	private String id;
	private int incomingTotal = 0;

	public void on(CallCreatedEvent event) {
		id = event.getId();
		incomingTotal += 1;
	}
}
