package org.brahms5.calendar.client;
import groovy.util.logging.Slf4j
import asg.cliche.Command
import asg.cliche.ShellFactory

@Slf4j
public class ClientMain {
	Client mClient = null;
	public static void main(String[] args) throws IOException {
		new ClientMain().run();
	}
	
	public void run() throws IOException
	{
        ShellFactory.createConsoleShell("CalendarClient", "", this)
            .commandLoop()
	}
	
	/*
	 *  CLI COMMANDS ARE BELOW
	 * 
	 */
	@Command
	public String status()
	{
		return "No status atm"
	}
	
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
	public String retrieveEvent()
	{
		if (mClient == null) return "Please log in"
		try
		{
			return "Success"
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}

	@Command
	public String list()
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
			mClient.connectCalendar(user)
			return "Success"
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
			mClient.createCalendar(user)
			return "Success"
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	
	
	// Calendar methods
	@Command
	public String retrieveEvents(String user, String dateStart, String dateEnd)
	{
		if (mClient == null) return "Please log in"
		try
		{
			return "Success"
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	
	@Command
	public String retrieveEvents(String dateStart, String dateEnd)
	{
		if (mClient == null) return "Please log in"
		try
		{
			return "Success"
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	@Command
	public String scheduleEvent( 
		String dateStart,
		String dateEnd, 
		String description, 
		String accessControl)
	{
		if (mClient == null) return "Please log in"
		try
		{
			return "Success"
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	@Command
	public String scheduleEvent(String userList, 
		String dateStart,
		String dateEnd, 
		String description, 
		String accessControl)
	{
		if (mClient == null) return "Please log in"
		try
		{
			return "Success"
		}
		catch(ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	
	
	@Command
	public String showLoggedInUsers()
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
		catch (ex)
		{
			ex.printStackTrace()
			return "ERROR"
		}
	}
	
	

}
