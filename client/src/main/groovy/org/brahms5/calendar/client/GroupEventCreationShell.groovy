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
	
	
	@Command(description="Adds an array of users to this group event")
	public void addUser(String... usernames) {
		def users = usernames.collect {
			return new User(it)
		}
		(event as GroupEvent).getMembers().addAll(users);
		canceled = false
		println status()
	}

	@Command(description="Removes an array of users to this group event")
	public void removeUser(String... usernames) {
		def users = usernames.collect {
			return new User(it)
		}
		(event as GroupEvent).getMembers().removeAll(users);
		canceled = false
		println status()
	}

	@Command(description="Removes all currently assigned users to this group event")
	public void clearUsers() {
		(event as GroupEvent).getMembers().clear();
		canceled = false
		println status()
	}
}
