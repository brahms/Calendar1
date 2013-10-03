package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.User

import asg.cliche.Command
import asg.cliche.Shell;
import asg.cliche.ShellDependent
import asg.cliche.ShellFactory

@Slf4j
class CalendarServiceShell implements ShellDependent{
	final def trace = {
		str -> log.trace str
	}
	
	final def warn = {
		str -> log.warn str
	}
	Shell mShell
	Client mClient
	User mUser
	
	public CalendarServiceShell(Client client, User user)
	{
		mClient = client
		mUser = user
	}
	
	
	
	@Command
	public String createEvent()
	{
		log.trace "Creating event shell"
		def shell = new EventCreationShell(mUser)
		
		ShellFactory.createSubshell("Event Creation", mShell, "", shell).commandLoop()
		
		if (!shell.isCanceled()) {
			def event = shell.getEvent()
			def userList = shell.getUserList().collect {
				return new User(it)
			}
			log.trace "Got back event: $event"
			try { 
				event.validate()
				return mClient.scheduleEvent(userList, event)
			}
			catch (ex) {
				return "ERROR: " + ex.toString()
			}
		}
		else {
			return "Canceled"
		}
	}
	
	
	@Command
	public String retrieveSchedule()
	{
		def shell = new RetrieveScheduleShell()
		ShellFactory.createSubshell("Retrieve Schedule", mShell, "", shell).commandLoop()
	
		if (!shell.isCanceled()) {
			log.trace "Got back event: $shell"
			try
			{
				return mClient.retrieveSchedule(shell.getUserName(), shell.getTimeInterval())
			}
			catch (ex) {
				return "ERROR: " + ex.toString()
			}
		}
		else {
			return "Canceled"
		}
	}



	@Override
	public void cliSetShell(Shell shell) {
		mShell = shell
		
	}
}
