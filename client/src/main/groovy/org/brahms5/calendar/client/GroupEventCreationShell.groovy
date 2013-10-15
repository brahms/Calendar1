package org.brahms5.calendar.client

import groovy.util.logging.Slf4j;

import java.util.Set;

import org.brahms5.calendar.domain.GroupEvent
import org.brahms5.calendar.domain.User;

import asg.cliche.Command;

@Slf4j
public class GroupEventCreationShell extends EventCreationShell {
	
	public GroupEventCreationShell(User owner) {
		super(owner)
		mOwner = owner;
		event = new GroupEvent()
		event.setOwner(mOwner)
	}
	
	@Override
	public String setEventType(String eventType) {
		println status()
		return "Ignored"
	}
	
	
	@Command
	public void addUser(String... usernames) {
		(event as GroupEvent).getMembers().addAll(usernames);
		canceled = false
		println status()
	}

	@Command
	public void removeUser(String... usernames) {
		(event as GroupEvent).getMembers().removeAll(usernames);
		canceled = false
		println status()
	}

	@Command
	public void clearUsers() {
		(event as GroupEvent).getMembers().clear();
		canceled = false
		println status()
	}
}
