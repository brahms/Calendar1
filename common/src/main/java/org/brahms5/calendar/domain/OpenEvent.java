package org.brahms5.calendar.domain;

import java.util.ArrayList;
import java.util.List;


public class OpenEvent extends Event{
	private static final long serialVersionUID = -6363073056522817988L;
	List<GroupEvent> events = new ArrayList<GroupEvent>();
	
	public List<GroupEvent> getEvents() {
		return events;
	}
	public void setEvents(List<GroupEvent> events) {
		this.events = events;
	}
	public OpenEvent()
	{
		setAccessControlMode(AccessControlMode.OPEN);
	}
	@Override
	public String toString()
	{
		return String.format("OpenEvent[events: %s, access: %s description: %s timeStart: %s timeEnd: %s]", getEvents().size(), getAccessControlMode(), getDescription(), getTimeStart(), getTimeEnd());
	}
}
