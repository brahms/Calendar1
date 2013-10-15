package org.brahms5.calendar.client;
import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.User

import asg.cliche.Command
import asg.cliche.Shell
import asg.cliche.ShellDependent
import asg.cliche.ShellFactory

import com.hazelcast.map.proxy.MapProxyImpl

@Slf4j
public class ClientMain implements ShellDependent{
	Client mClient = null;
	Shell mShell = null;
	User mUser = null;
	public static void main(String[] args) throws IOException {
		new ClientMain().run();
	}
	
	public void run() throws IOException
	{
		createShutDownHook()
        ShellFactory.createConsoleShell("CalendarClient", "", this)
            .commandLoop()
			MapProxyImpl bla;
	}
	
	/*
	 *  CLI COMMANDS ARE BELOW
	 * 
	 */
	
	@Command
	public void exit()
	{
		mClient?.shutdown();
		println "Exiting"
		System.exit(0);
	}
	
	// Other stuff
	@Command
	public String login(String user)
	{
		if (mClient == null)
		{
			mClient = new Client(user)
			mClient.connect();
			mUser = new User(user)
			return "Logged in"
		}
		else
		{
			return "Already logged in"
		}
	}
	
	// Calendar Manager commands
	
	@Command
	public String listCalendars()
	{
		if (mClient == null) return "Please log in"
		
		try
		{
			def builder = new StringBuilder()
			mClient.listCalendars().each {
				builder.append("\t${it.getName()}\n")
			}
			return "Calendars: \n" + builder.toString()
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}

	@Command
	public String whosLoggedIn()
	{
		if (mClient == null) return "Please log in"
		try
		{
			def builder = new StringBuilder()
			mClient.getLoggedInUsers().each {
				builder.append("User: ${it}\n")
			}
			
			return builder.toString()
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	
	@Command
	public String calendarServices()
	{
		if (mClient == null) return "Please log in"
		try
		{
			def shell = new CalendarServiceShell(mClient, mUser)
			ShellFactory.createSubshell("CalendarService", mShell, "", shell).commandLoop()
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	
	@Command 
	public String createCalendar(String user)
	{
		if (mClient == null) return "Please log in"
		try
		{
			return mClient.createCalendar(user)
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
	
	private void createShutDownHook()
	{
		final def self = this
		Runtime.getRuntime().addShutdownHook(new Thread({
			log.trace "Shutting down client due to shutdown hook"
			self.mClient?.shutdown()
		}));
	}
}
