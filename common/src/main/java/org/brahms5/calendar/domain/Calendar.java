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
	/**
	 * Returns a cleaned list of events for a user given an interval
	 * @param user
	 * @param interval
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public List<Event> getEvents(User user, TimeInterval interval)
	{
		List<Event> returnedEvents = new ArrayList<Event>();
		
		for (Event event : getEvents()) 
		{
			if (interval.contains(event.getTimeInterval()) &&
				event.canBeAccessedBy(user)) {
				try {
					returnedEvents.add(event.cleanFor(user));
				} catch (CloneNotSupportedException e) {}
			}
		}
		
		return returnedEvents;
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
	
	public String debugString()
	{
		StringBuilder b = new StringBuilder();
		
		b.append(String.format("--------%s's Calendar (%d events)--------\n", getUser().getName(), getEvents().size()));
		for (Event event : getEvents()) {
			b.append(String.format("\n------------------------------------\n"));
			b.append(event.debugString());
			b.append(String.format("\n------------------------------------\n"));
		}
		
		return b.toString();
	}

	
}
