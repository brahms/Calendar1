package org.brahms5.calendar.client

import groovy.time.TimeCategory
import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.*
@Slf4j
class AppointmentAlerter extends Thread {

	final def trace = { msg ->
		log.trace("${mClient.getClientUser()}: $msg")
	}
	def mClient
	public AppointmentAlerter(client) {
		mClient = client;
	}
	
	
	private Long lastCheck = 0

	@Override
	public void run() {
		use(TimeCategory)
		{
			lastCheck = System.currentTimeMillis() - (4.minutes.toMilliseconds())
			
			lastCheck = lastCheck - (lastCheck % 1.second.toMilliseconds())
			trace "Starting"
			try 
			{
				while(true) 
				{
					
					final def currentTime = System.currentTimeMillis();
					final def interval = new TimeInterval(lastCheck, currentTime)
					
					try
					{	
						final Calendar calendar = mClient.getClientCalendar()
						
						final def events = calendar.getEventsStartingWithin( interval )
						lastCheck = currentTime
						
						if (events.isEmpty() == false) {
							events.each {
								alert(it)
							}
						}
					}
					catch(ex)
					{
						if (ex instanceof InterruptedException) throw ex
						trace "Got an exception: $ex"
					}
					sleep(1.second.toMilliseconds())
				}
			}
			catch(InterruptedException ex)
			{
				
			}
			trace "Done"
		}
	}
	
	protected void alert(Event event) 
	{
		println "\n-----------Appointment Alert------------------\n${event.debugString()}\n---------------------------------------"
	}

	
}
