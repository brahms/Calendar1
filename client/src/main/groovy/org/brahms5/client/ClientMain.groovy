package org.brahms5.client;
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
		mClient = new Client();
		
		log.trace "connecting"
		mClient.on("connect", {
			f -> 
			log.trace("connected");
		});
		mClient.connect();
		
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
		mClient.shutdown();
		println "Exiting"
		System.exit(0);
	}
	
	// Calendar Manager commands
	
	@Command
	public String retrieveEvent()
	{
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
	public String connectCalendar(String user)
	{
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
	public String createCalendar(String user)
	{
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
	
	

}
