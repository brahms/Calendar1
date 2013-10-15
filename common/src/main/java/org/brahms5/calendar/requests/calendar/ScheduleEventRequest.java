package org.brahms5.calendar.requests.calendar;

import org.brahms5.calendar.domain.Event;
import org.brahms5.calendar.domain.User;

public class ScheduleEventRequest extends ACalendarRequest {
	Event event = null;
	public ScheduleEventRequest(String uuid, String id, User user) {
		super(uuid, id, user);
	}
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	private static final long serialVersionUID = 1077771650478136988L;

}
