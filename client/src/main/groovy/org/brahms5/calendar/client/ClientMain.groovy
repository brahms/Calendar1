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
	boolean mMedusa = false
	public static void main(String[] args) throws IOException {
		new ClientMain().run(null != args.find{
			return it.equals("medusa")
		});
		System.setProperty("hazelcast.logging.type", "slf4j");
	}
	
	public void run(boolean medusa) throws IOException
	{
		mMedusa = medusa
		createShutDownHook()
        ShellFactory.createConsoleShell("CalendarClient", "", this)
            .commandLoop()
	}
	
	/*
	 *  CLI COMMANDS ARE BELOW
	 * 
	 */
	
	@Command(description="Exits the program")
	public void exit()
	{
		mClient?.shutdown();
		println "Exiting"
		System.exit(0);
	}
	
	// Other stuff
	@Command(description="Log into the calendar cluster")
	public String login(String user)
	{
		if (mClient == null)
		{
			mClient = new Client(user)
			mClient.connect(mMedusa);
			mUser = new User(user)
			return "Logged in"
		}
		else
		{
			return "Already logged in"
		}
	}
	
	// Calendar Manager commands
	
	@Command(description="List the calendars in the cluster")
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
	
	@Command(description="Show who's logged into the cluster")
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
	
	@Command(description="Use calendar services")
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
	
	@Command(description="Create a calendar for a given user")
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
	
	private void createShutDownHook()
	{
		final def self = this
		Runtime.getRuntime().addShutdownHook(new Thread({
			log.trace "Shutting down client due to shutdown hook"
			self.mClient?.shutdown()
		}));
	}
}
