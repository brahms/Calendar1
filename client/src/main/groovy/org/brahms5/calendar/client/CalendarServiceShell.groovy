package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.GroupEvent
import org.brahms5.calendar.domain.User

import asg.cliche.Command
import asg.cliche.Shell
import asg.cliche.ShellDependent
import asg.cliche.ShellFactory

@Slf4j
class CalendarServiceShell implements ShellDependent{
	final def trace = {
		str -> log.trace "$mUser: $str"
	}
	
	final def warn = {
		str -> log.warn "$mUser: $str"
	}
	Shell mShell
	Client mClient
	User mUser
	
	public CalendarServiceShell(Client client, User user)
	{
		mClient = client
		mUser = user
	}
	
	
	
	@Command(description="Create a public, private, or open event.")
	public String createEvent()
	{
		log.trace "Creating event shell"
		def shell = new EventCreationShell(mUser)
		
		ShellFactory.createSubshell("Event Creation", mShell, "", shell).commandLoop()
		
		if (!shell.isCanceled()) {
			def event = shell.getEvent()
			log.trace "Got back event: $event"
			try { 
				event.validate()
				return mClient.scheduleEvent(event)
			}
			catch (ex) {
				return "ERROR: " + ex.toString()
			}
		}
		else {
			return "Canceled"
		}
	}
	@Command(description="Creates a group event")
	public String createGroupEvent()
	{
		log.trace "Creating group event shell"
		def shell = new GroupEventCreationShell(mUser)
		
		ShellFactory.createSubshell("Group Event Creation", mShell, "", shell).commandLoop()
		
		if (!shell.isCanceled()) {
			def event = shell.getEvent()
			log.trace "Got back event: $event"
			try {
				event.validate()
				return mClient.scheduleEvent(event)
			}
			catch (ex) {
				return "ERROR: " + ex.toString()
			}
		}
		else {
			return "Canceled"
		}
	}
	
	
	@Command(description="Retrieves a user's schedule")
	public void retrieveSchedule()
	{
		def shell = new RetrieveScheduleShell(mUser)
		ShellFactory.createSubshell("Retrieve Schedule", mShell, "", shell).commandLoop()
	
		if (!shell.isCanceled()) {
			try
			{
				def list =  mClient.retrieveSchedule(shell.getUserName(), shell.getTimeInterval())
				
				list.each {
					Event event -> println "\n---------------------------------------------------------------------\n${event.debugString()}\n-----------------------------------------------------------------\n"
				}
			}
			catch (ex) {
				println "ERROR: " + ex.getMessage()
			}
		}
		else {
			println "Canceled"
		}
	}
	
	
	@Command(description="Dumps the state of your current calendar")
	public String dump()	{
		if (mClient == null) return "Please log in";
		
		try
		{
			return mClient.dump()
		}
		catch(ex)
		{
			return "ERROR: $ex"
		}
	}
	
	@Override
	public void cliSetShell(Shell shell) {
		mShell = shell
		
	}
}
