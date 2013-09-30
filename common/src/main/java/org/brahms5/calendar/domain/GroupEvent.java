package org.brahms5.calendar.domain;

import java.util.List;

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
		return String.format("GroupEvent[access: %s description: %s timeInterval: %s]", getAccessControlMode(), getDescription(), getTimeInterval());
	}
}
