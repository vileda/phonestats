package phonestats.event;

import io.resx.core.event.DistributedEvent;
import lombok.Getter;
import lombok.Setter;
import phonestats.aggregate.Dashboard;

import static phonestats.Constants.UPDATE_DASHBOARD_EVENT_ADDRESS;

@Getter
@Setter
public class UpdateDashboardEvent extends DistributedEvent {
	private Dashboard dashboard;

	public UpdateDashboardEvent() {
		super(UPDATE_DASHBOARD_EVENT_ADDRESS);
	}

	public UpdateDashboardEvent(Dashboard dashboard) {
		super(UPDATE_DASHBOARD_EVENT_ADDRESS);
		this.dashboard = dashboard;
	}
}
