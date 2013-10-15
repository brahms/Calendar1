package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User

import asg.cliche.Command

@Slf4j
class RetrieveScheduleShell extends AShell{
	
	TimeInterval timeInterval = new TimeInterval();
	String userName = ""
	boolean canceled = false

	public RetrieveScheduleShell(User user) 
	{
		userName = user.getName()
	}
	@Command
	public void setTimeStart(String timeString) {
		getTimeInterval().setTimeStart(getTime(timeString))
		status()
	}

	@Command
	public void setTimeEnd(String timeString) {
		getTimeInterval().setTimeEnd(getTime(timeString))
		status()
	}
	
	@Command
	public void setUser(String name)
	{
		userName = name
		status()
	}
	
	public String getUser()
	{
		return userName
	}
	
	@Command
	public void status() {
		println """User: $userName
TimeInterval: ${timeInterval.debugString()}"""
	}
	
	
	public TimeInterval getTimeInterval()
	{
		return timeInterval
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
