package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.OpenEvent
import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.domain.Event.AccessControlMode

import asg.cliche.Command
import asg.cliche.Param


@Slf4j
public class EventCreationShell extends AShell{

	
	Event event = new Event()
	boolean canceled = false
	User mOwner

	@Command(description="Sets the event's type")
	public String setEventType(
		@Param(name="eventType", description="Event type: (PUBLIC, PRIVATE or OPEN)")
		String eventType) {
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
	
	@Command(description="Sets the time interval of the event")
	public void setTime(
		@Param(name="timeStart", description="The date/time to start the event")
		String timeStartString, 
		
		@Param(name="timeEnd", description="The date/time to end the event")
		String timeEndString) {
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

	@Command(description="Sets the description of the event")
	public void setDescription(String... description) {
		canceled = false
		event.setDescription((description as List).join(" "))
		println status()
	}

	
	@Command(description="Resets the event to a null event")
	public void clear()
	{
		canceled = false
		event = event.getClass().newInstance()
		event.setOwner(mOwner)
		println status()
	}

	@Command(description="Prints out the current event data")
	public String status() {
		return isCanceled() ? "Canceled" : """\
Event: 
${event.debugString()}
"""
	}

	@Command(description="Cancels the current event")
	public String cancel() {
		canceled = true
		return "You have to type exit to stop the shell"
	}
	
	
	boolean isCanceled()
	{
		return canceled
	}
}