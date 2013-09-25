package org.brahms5.client;
import groovy.util.logging.Slf4j

import org.vertx.java.core.Vertx
import org.vertx.java.core.VertxFactory

import asg.cliche.Command
import asg.cliche.ShellFactory

@Slf4j
public class ClientMain {
	Vertx mVertx = null;
	AsyncClient mAsyncClient = null;
	public static void main(String[] args) throws IOException {
		new ClientMain().run();

	}
	
	public void run() throws IOException
	{
		mVertx = VertxFactory.newVertx()
		mAsyncClient = new AsyncClient(mVertx);
		
		log.trace "connecting"
		mAsyncClient.getEventEmitter().on("connect", {
			f -> 
			log.trace("connected");
		});
		mAsyncClient.connect();
		
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
		println "Exiting"
		System.exit(0);
	}


}
