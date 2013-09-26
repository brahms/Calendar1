package org.brahms5.server

import groovy.util.logging.Slf4j
import asg.cliche.Command
import asg.cliche.ShellFactory

@Slf4j
class ServerMain {
	Server mAsyncServer = null;
	public static main(args) 
	{
		new ServerMain().run();
	}
	
	public run()
	{
		log.trace "run()"
		mAsyncServer = new Server();
		
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
		log.trace "status()"
	}
	
	@Command
	public void exit()
	{
		mAsyncServer.shutdown();
		log.trace "exit()"
		println "Exiting."
		System.exit(0);
	}
}
