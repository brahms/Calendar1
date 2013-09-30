package org.brahms5.calendar.client

import org.brahms5.calendar.domain.TimeInterval

import asg.cliche.Command;

class RetrieveScheduleShell {
	TimeInterval timeInterval = new TimeInterval();
	String userName = ""
	boolean canceled = false

	@Command
	public void setTimeStart(Long time) {
		getTimeInterval().setTimeStart(time)
	}

	@Command
	public void setTimeEnd(Long time) {
		getTimeInterval().setTimeEnd(time)
	}
	
	@Command
	public void setUser(String name)
	{
		userName = name
	}
	
	public String getUser()
	{
		return userName
	}
	
	@Command
	String status() {
		return "TimeInterval: $timeInterval, User: $userName"
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
