package org.brahms5.calendar.responses.calendars;

import java.util.List;

import org.brahms5.calendar.domain.Event;
import org.brahms5.calendar.domain.TimeInterval;
import org.brahms5.calendar.responses.Response;

public class RetrieveScheduleResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 864235222469601543L;
	List<Event> events = null;
	TimeInterval timeInterval = null;
	public RetrieveScheduleResponse(String id, String error) {
		super(id, error);
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}
	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

}
