package org.brahms5.calendar.server

import groovy.util.logging.Slf4j
import asg.cliche.Command
import asg.cliche.ShellFactory

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
	public void exit()
	{
		mServer?.shutdown();
		log.trace "exit()"
		println "Exiting."
		System.exit(0);
	}
}
