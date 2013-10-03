package org.brahms5.calendar.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class GroupEvent extends Event {
	private static final long serialVersionUID = -6063030718268590528L;
	public List<User> getMembers() {
		return members;
	}
	public void setMembers(List<User> members) {
		this.members = members;
	}
	public OpenEvent getParent() {
		return parent;
	}
	public void setParent(OpenEvent parent) {
		this.parent = parent;
	}
	List<User> members;
	OpenEvent parent;
	
	public GroupEvent()
	{
		setAccessControlMode(AccessControlMode.GROUP);
	}
	@Override
	public String toString()
	{
		return String.format("GroupEvent[access: %s description: %s timeInterval: %s, members %s]", getAccessControlMode(), getDescription(), getTimeInterval(), StringUtils.join(getMembers(), ", "));
	}
	@Override
	void validate() throws Exception {
		// TODO Auto-generated method stub
		super.validate();
		
		if (getMembers() == null) throw new Exception("Members list is null");
		if (getMembers().isEmpty()) throw new Exception("Members list is empty");
		if (getMembers().contains(getOwner()) == false) throw new Exception("Owner isn't a member");
		if (getParent() == null) throw new Exception("Parent is null");
		if (getParent().getAccessControlMode() != AccessControlMode.OPEN) throw new Exception ("Parent event isn't an open event");
	}
	
	
	
	@Override
	public GroupEvent cleanFor(User user) throws CloneNotSupportedException {
		GroupEvent event = (GroupEvent) super.cleanFor(user);
		log.trace(String.format("Cleaning for: %s", user));
		
		if (!getMembers().contains(user)) 
		{
			log.trace("Will scrub");
			event.setMembers(new ArrayList<User>());
			event.setDescription("");
			event.setOwner(null);
		}
		
		return event;
	}
	@Override
	public boolean addTo(Calendar calendar) {
		log.trace(String.format("addTo(%s)", calendar));
		for (Event event : calendar.getEvents()) {
			if(event instanceof OpenEvent && event.contains(this)) {
				OpenEvent openEvent = (OpenEvent) event;
				for (GroupEvent groupEvent : openEvent.getEvents()) {
					if (groupEvent.conflictsWith(this)) {
						log.trace(String.format("Can't be added because conflicts with: %s", groupEvent));
						return false;
					}
				}
				
				log.trace(String.format("Adding to OpenEvent: %s", openEvent));
				this.setParent(openEvent);
				openEvent.getEvents().add(this);
				log.trace("Sorting.");
				Collections.sort(openEvent.getEvents());
				log.trace("Done");
				return true;
				
			}
			log.trace("No open events found that can contain this.");
			return false;
		}
		return false;
	}
	
	
	@Override
	public String debugString()
	{
		return String.format(super.debugString() + String.format(
		"\tMembers: [%s]", StringUtils.join(getMembers(), ", ")
		));
	}
	
	
	
	
}
