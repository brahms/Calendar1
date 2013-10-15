package org.brahms5.calendar.client

import groovy.time.TimeCategory;
import groovy.util.logging.Slf4j;
import org.brahms5.calendar.domain.*
@Slf4j
class AppointmentAlerter extends Thread {

	final def trace = { msg ->
		log.trace(msg)
	}
	Client mClient
	public AppointmentAlerter(Client client) {
		mClient = client;
	}
	
	Long lastCheck = 0
	@Override
	public void run() {
		trace "Starting"
		try 
		{
			def currentTime = System.currentTimeMillis();
			trace "Checking for an event."
			Calendar calendar = mClient.getClientCalendar()
			final def events = calendar.getEvents(mClient.getClientUser(), new TimeInterval(lastCheck, currentTime))
			lastCheck = currentTime
			
			if (events.isEmpty() == false) {
				events.each {
					alert(it)
				}
			}
			sleep(5000)
		}
		catch(ex)
		{
			trace "Caught an exception"	
		}
		trace "Done"
	}
	
	protected void alert(Event event) 
	{
		println "Appointment Alert: ${event.debugString()}"
	}

	
}
