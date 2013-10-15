package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.OpenEvent
import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.domain.Event.AccessControlMode

import asg.cliche.Command


@Slf4j
public class EventCreationShell extends AShell{

	
	Event event = new Event()
	boolean canceled = false
	User mOwner

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
			case "public":
				event = new Event()
				event.setAccessControlMode(AccessControlMode.PUBLIC)
				break
			default:
				return "Unknown type: $eventType"
		}
		event.setDescription(oldEvent.getDescription())
		event.setTimeInterval(oldEvent.getTimeInterval())
		event.setOwner(mOwner);
		println status()
		return "Ok"
	}
	
	
	public EventCreationShell(User owner) {
		mOwner = owner;
		event = new Event()
		event.setOwner(mOwner)
	}
	
	@Command
	public void setTime(String timeStartString, String timeEndString) {
		Long timeStart = getTime(timeStartString)
		Long timeEnd = getTime(timeEndString)
		if (timeStart != null && timeEnd != null) {
			log.trace "Setting event."
			event.setTimeInterval(new TimeInterval(timeStart, timeEnd))
			canceled = false
			println status()
		}
		else {
			log.trace "No time interval to set event with."
		}
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
		event = event.getClass().newInstance()
		event.setOwner(mOwner)
		println status()
	}

	@Command
	public String status() {
		return isCanceled() ? "Canceled" : """\
Event: 
${event.debugString()}
"""
	}

	@Command
	public String cancel() {
		canceled = true
		return "You have to type exit to stop the shell"
	}
	
	
	boolean isCanceled()
	{
		return canceled
	}
}