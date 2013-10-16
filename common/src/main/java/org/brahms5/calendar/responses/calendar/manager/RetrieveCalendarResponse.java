package org.brahms5.calendar.responses.calendar.manager;

import org.brahms5.calendar.domain.Calendar;
import org.brahms5.calendar.responses.Response;

public class RetrieveCalendarResponse extends Response{
	private static final long serialVersionUID = 2109016485208307383L;
	private Calendar calendar;
	public RetrieveCalendarResponse(String id, String error) {
		super(id, error);
	}
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	@Override
	public String toString()
	{
		return String.format("RetrieveCalendarResponse[calendar user: %s, events: %s]", getCalendar(), getCalendar().getEvents());
	}
}
