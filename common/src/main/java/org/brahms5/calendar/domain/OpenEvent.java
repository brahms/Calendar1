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
		return String.format("OpenEvent[events: %s, access: %s description: %s timeInterval: %s]", getEvents().size(), getAccessControlMode(), getDescription(), getTimeInterval());
	}
	@Override
	public Event cleanFor(User user) throws CloneNotSupportedException {
		OpenEvent cleanedEvent =  (OpenEvent) super.cleanFor(user);
		List<GroupEvent> newList = new ArrayList<GroupEvent>(getEvents().size());
		for (GroupEvent groupEvent : getEvents()) {
			newList.add(groupEvent.cleanFor(user));
		}
		
		cleanedEvent.setEvents(newList);
		
		return cleanedEvent;
	}
	
	@Override
	public String debugString()
	{
		StringBuilder b = new StringBuilder();
		for (GroupEvent event : getEvents()) 
		{
			b.append(event.debugString() + "\n\n");
		}
		return super.toString() + b.toString();
		
	}
	
	
	
}
