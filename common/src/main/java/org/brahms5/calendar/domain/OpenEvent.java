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
		return String.format("OpenEvent[%s, Total Group Events: %s]", super.toString(), getEvents().size());
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
		StringBuilder b = new StringBuilder("\tGroup Events:");
		for (GroupEvent event : getEvents()) 
		{
			b.append("\n\t\t" + event.debugString().replace("\n", "\n\t\t") + "\n");
		}
		return super.debugString() + b.toString();
		
	}
	
	
	
}
