package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.GroupEvent
import org.brahms5.calendar.domain.OpenEvent
import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.domain.Event.AccessControlMode

import asg.cliche.Command


@Slf4j
public class EventCreationShell {
	
	Long getTime(String timeString) {
		log.trace "Converting $timeString to date"
		if (timeString.isNumber()) {
			log.trace "It's a number."
			return Long.parseLong(timeString)
		}
		else if(timeString.contains(".")){
			def binding = new Binding();
			def sh = new GroovyShell(binding)
			def command = """\
	use(groovy.time.TimeCategory) {
	   return ${timeString}
	}"""			
			log.trace "Using command: $command"
			try {
				Date date =  sh.evaluate(command) as Date
				log.trace "Evaluated: $date"
				Long time = date.getTime();
				log.trace "Returning $time"
				return time
			}
			catch(ex) {
				log.warn "Can't parse the command."
				return null
			}
		}
		else {
			log.warn "Bad command."
			println "Error can't parse: $timeString"
			return null
		}
	}
	
	Event event = new Event()
	Set<String> userList = new TreeSet<String>()
	boolean canceled = true
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
${event.debugString()}
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