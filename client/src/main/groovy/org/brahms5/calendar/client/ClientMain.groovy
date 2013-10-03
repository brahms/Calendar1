package org.brahms5.calendar.client;
import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.GroupEvent
import org.brahms5.calendar.domain.OpenEvent
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.domain.Event.AccessControlMode

import com.hazelcast.map.proxy.MapProxyImpl;

import asg.cliche.Command
import asg.cliche.Shell
import asg.cliche.ShellDependent
import asg.cliche.ShellFactory

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
				builder.append("\t$it\n")
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
	public String connectCalendar(String user)
	{
		if (mClient == null) return "Please log in"
		try
		{
			def error = mClient.connectCalendar(user)
			mUser = new User(user)
			if (error == null)
			{
				def shell = new CalendarServiceShell(mClient, mUser)
				ShellFactory.createSubshell("$user", mShell, "", shell).commandLoop()
				mClient.disconnectCalendar()
			}
			else 
			{
				return error
			}
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
	
	
	

}
