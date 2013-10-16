package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User

import asg.cliche.Command
import asg.cliche.Param

@Slf4j
class RetrieveScheduleShell extends AShell{
	
	TimeInterval timeInterval = new TimeInterval();
	String userName = ""
	boolean canceled = false

	public RetrieveScheduleShell(User user) 
	{
		userName = user.getName()
	}
	@Command(description="Set the start time of the interval to search for")
	public void setTimeStart(String timeString) {
		getTimeInterval().setTimeStart(getTime(timeString))
		status()
	}
	
	@Command(description="Set the end time of the interval to search for")
	public void setTimeEnd(String timeString) {
		getTimeInterval().setTimeEnd(getTime(timeString))
		status()
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
			setTimeInterval(new TimeInterval(timeStart, timeEnd))
			canceled = false
			println status()
		}
		else {
			log.trace "No time interval to set event with."
		}
	}

	@Command(description="Set user of the search request")
	public void setUser(String name)
	{
		userName = name
		status()
	}
	
	public String getUser()
	{
		return userName
	}
	
	@Command(description="Print out the current request")
	public void status() {
		println """User: $userName
TimeInterval: ${timeInterval.debugString()}"""
	}
	
	
	public TimeInterval getTimeInterval()
	{
		return timeInterval
	}
	
	
	@Command(description="Cancel the current request")
	public String cancel() {
		canceled = true
		return "You have to type exit to stop the shell"
	}
	
	boolean isCanceled()
	{
		return canceled
	}
}
