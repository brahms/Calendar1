package org.brahms5.calendar.server

import groovy.util.logging.Slf4j
import asg.cliche.Command
import asg.cliche.ShellFactory
import org.brahms5.calendar.domain.Calendar;

@Slf4j
class ServerMain {
	Server mServer = null;
	public static main(args) 
	{
		new ServerMain().run();
	}
	
	public run()
	{
		log.trace "run()"
		try
		{
			mServer = new Server();
			createShutDownHook()
		}
		catch(ex)
		{
			log.warn "error creating server", ex
			exit()
		}
		
		ShellFactory.createConsoleShell("CalenderServer", "", this)
			.commandLoop()
	}
	
	/**
	 * CLI Commands
	 * 
	 */
	
	
	@Command
	public String status()
	{
		print mServer.getStatus()
	}
	
	@Command 
	public String dump(String name)
	{
		final StringBuilder b = new StringBuilder()
		final Calendar calendar = mServer.getCalendar(name);
		
		return calendar?.debugString() ?: "Not found"
	}
	
	@Command 
	public String dump()
	{
		final StringBuilder b = new StringBuilder()
		mServer.getAllCalendars().each { Calendar cal ->
			b.append(cal.debugString() + "\n")
		}
	}
	
	@Command
	public void exit()
	{
		mServer?.shutdown();
		log.trace "exit()"
		println "Exiting."
		System.exit(0);
	}
	
	@Command
	public String obliterate()
	{
		try
		{
			mServer.obliterate()
			return "Success"
		}
		catch(ex)
		{
			log.warn "Error trying to obliterate", ex
			"Error"
		}
	}
	
	private void createShutDownHook()
	{
		log.trace "Creating shutdown hook."
		final def self = this
		Runtime.getRuntime().addShutdownHook(new Thread({
			log.trace "Shutting down server due to shutdown hook"
			self.mServer?.shutdown()
		}));
	}
}
