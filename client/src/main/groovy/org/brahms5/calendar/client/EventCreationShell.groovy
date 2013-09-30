package org.brahms5.calendar.client

import java.util.Set;

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.GroupEvent
import org.brahms5.calendar.domain.OpenEvent
import org.brahms5.calendar.domain.Event.AccessControlMode

import asg.cliche.Command


class EventCreationShell {
	Event event = new Event()
	Set<String> userList = new TreeSet<String>()
	boolean canceled = true

	@Command
	public String setEventType(String eventType) {
		canceled = false
		def oldEvent = event
		switch(eventType.toLowerCase()) {
			case "open":
				event = new OpenEvent()
				break
			case "private":
				event = new Event()
				event.setAccessControlMode(AccessControlMode.PRIVATE)
				break
			case "group":
				event = new GroupEvent()
				break
			case "public":
				event = new Event()
				event.setAccessControlMode(AccessControlMode.PUBLIC)
				break
			default:
				return "Unknown type: $eventType"
		}
		event.setDescription(oldEvent.getDescription())
		event.setTimeInterval(oldEvent.getTimeInterval())
		println status()
		return "Ok"
	}
	@Command
	public void setTimeStart(Long time) {
		event.getTimeInterval().setTimeStart(time)
		canceled = false
		println status()
	}

	@Command
	public void setTimeEnd(Long time) {
		canceled = false
		event.getTimeInterval().setTimeEnd(time)
		println status()
	}

	@Command
	public void setDescription(String... description) {
		canceled = false
		event.setDescription((description as List).join(" "))
		println status()
	}

	
	@Command
	public void clear()
	{
		canceled = false
		event = new Event()
		clearUsers()
		println status()
	}

	@Command
	String status() {
		def usersStr = new StringBuilder()
		
		getUserList().each {
			usersStr.append("\t$it\n")
		}
		
		usersStr = getUserList().isEmpty() ? "\tEmpty" : usersStr.toString()
		return isCanceled() ? "Canceled" : """\
Event:
\tDescription: ${event.getDescription()}
\tTimeInterval: ${event.getTimeInterval()}
\tAccessControl: ${event.getAccessControlMode()}
Users: 
${usersStr}
"""
	}

	@Command
	public String cancel() {
		canceled = true
		return "You have to type exit to stop the shell"
	}
	
	@Command
	public void addUser(String username) {
		userList.add(username);
		canceled = false
		println status()
	}

	@Command
	public void removeUser(String username) {
		userList.remove(username);
		canceled = false
		println status()
	}

	@Command
	public void clearUsers() {
		userList.clear();
		canceled = false
		println status()
	}

	
	public Set<String>getUserList()
	{
		return userList
	}
	
	boolean isCanceled()
	{
		return canceled
	}
}