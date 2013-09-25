package org.brahms5.server

import groovy.util.logging.Slf4j

import org.vertx.java.core.Vertx
import org.vertx.java.core.VertxFactory

import asg.cliche.Command
import asg.cliche.ShellFactory

@Slf4j
class ServerMain {
	
	Vertx mVertx = null;
	AsyncServer mAsyncServer = null;
	public static main(args) 
	{
		new ServerMain().run();
	}
	
	public run()
	{
		log.trace "run()"
		mVertx = VertxFactory.newVertx();
		mAsyncServer = new AsyncServer(mVertx);
		
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
		return "No status atm: " + mVertx.toString();
	}
	
	@Command
	public void exit()
	{
		log.trace "exit()"
		println "Exiting."
		System.exit(0);
	}
}
