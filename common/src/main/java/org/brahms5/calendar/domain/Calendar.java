package org.brahms5.calendar.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Calendar implements Serializable{
	private static final long serialVersionUID = 8954015604549311498L;
	List<Event> events = new ArrayList<Event>();
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	User user;
	
	@Override
	public String toString()
	{
		return String.format("Calendar[user: %s events: %s]", getUser(), getEvents().size());
	}
	
}
