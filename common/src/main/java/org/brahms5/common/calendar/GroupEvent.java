package org.brahms5.common.calendar;

import java.util.List;

import org.brahms5.common.User;

public interface GroupEvent extends Event{
	public List<User> getMembers();
	public GroupEvent setMembers(List<User> user);
	public GroupEvent addMember(User user);
}
