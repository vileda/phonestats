package phonestats;

import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class Constants {
	public static final String CREATE_CALL_COMMAND_ADDRESS = "create.call.command";
	public static final String CALL_CREATED_EVENT_ADDRESS = "create.call.event";
	public static final String UPDATE_DASHBOARD_EVENT_ADDRESS = "update.dashboard.event";

	public static JsonObject getTodaysEventsQueryFor(String id) {
		ZonedDateTime startOfToday = ZonedDateTime.of(LocalDate.now(), LocalTime.MIN, ZoneId.of("UTC"));
		ZonedDateTime endOfToday = ZonedDateTime.of(LocalDate.now(), LocalTime.MAX, ZoneId.of("UTC"));

		return new JsonObject()
				.put("payload.id", id)
				.put("dateCreated", new JsonObject()
						.put("$gte", startOfToday.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
						.put("$lte", endOfToday.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
				);
	}
}
