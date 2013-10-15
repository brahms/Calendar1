package org.brahms5.calendar.domain;

import groovy.util.logging.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GroupEvent extends Event {
	private static final long serialVersionUID = -6063030718268590528L;

	public Set<User> getMembers() {
		return members;
	}

	public void setMembers(Set<User> members) {
		this.members = members;
	}

	public OpenEvent getParent() {
		return parent;
	}

	public void setParent(OpenEvent parent) {
		this.parent = parent;
	}

	Set<User> members = new TreeSet<User>();
	OpenEvent parent;

	public GroupEvent() {
		setAccessControlMode(AccessControlMode.GROUP);
	}

	@Override
	public String toString() {
		return String.format("GroupEvent[%s members %s]", super.toString(),
				StringUtils.join(getMemberNames(), ", "));
	}

	private Set<String> getMemberNames() {
		Set<String> names = new TreeSet<String>();
		for (User user : getMembers()) {
			names.add(user.getName());
		}
		return names;
	}

	@Override
	void validate() throws Exception {
		// TODO Auto-generated method stub
		super.validate();

		if (getMembers() == null)
			throw new Exception("Members list is null");
		if (getMembers().isEmpty())
			throw new Exception("Members list is empty");
		if (getMembers().contains(getOwner()) == false)
			throw new Exception("Owner isn't a member");
	}

	@Override
	public GroupEvent cleanFor(User user) throws CloneNotSupportedException {
		GroupEvent event = (GroupEvent) super.cleanFor(user);
		log.trace(String.format("Cleaning for: %s", user));

		if (!getMembers().contains(user)) {
			log.trace("Will scrub");
			event.setMembers(new TreeSet<User>());
			event.setDescription("");
			event.setOwner(null);
		}

		return event;
	}

	@Override
	public void addTo(Calendar calendar) throws Exception,
			ConflictingEventException {
		log.trace(String.format("addTo(%s)", calendar));
		
		for (Event event : calendar.getEvents()) {
			
			if (event instanceof OpenEvent && event.contains(this)) {
				OpenEvent openEvent = (OpenEvent) event;
				
				for (GroupEvent groupEvent : openEvent.getEvents()) {
				
					if (groupEvent.conflictsWith(this)) {
					
						log.trace(String.format(
								"Can't be added because conflicts with: %s",
								groupEvent));
						
						throw new ConflictingEventException(groupEvent);
					}
				}

				log.trace(String.format("Adding to OpenEvent: %s", openEvent));
				
				this.setParent(openEvent);
				openEvent.getEvents().add(this);
				
				log.trace("Sorting.");
				
				Collections.sort(openEvent.getEvents());
				
				log.trace("Done");
				
				return;

			}
			String error = "No open events found that can contain this.";
			log.trace(error);
			throw new Exception(error);
		}
	}

	@Override
	public String debugString() {
		Set<String> names = new TreeSet<String>();
		for (User user : getMembers()) {
			names.add(user.getName());
		}
		return String.format(super.debugString()
				+ String.format("\tMembers: [%s]",
						StringUtils.join(names, ", ")));
	}

	@Override
	public GroupEvent setOwner(User user) {
		super.setOwner(user);
		if (null != user && getMembers().contains(user) == false)
			getMembers().add(user);
		return this;
	}

}
