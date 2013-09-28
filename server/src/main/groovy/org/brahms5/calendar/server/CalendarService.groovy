package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import org.brahms5.calendar.requests.ARequest

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue
import org.brahms5.calendar.requests.calendar.*

@Slf4j
class CalendarService implements Runnable {
	IQueue mCalendarServiceQueue
	IMap mCalendarMap
	IMap mConnectMap
	HazelcastInstance mHazlecastFrontend
	Integer requestsServed = 0
	public CalendarService(IQueue serviceQueue, IMap calendarMap, IMap connectMap, HazelcastInstance instance)
	{
		mCalendarMap = calendarMap
		mCalendarServiceQueue = serviceQueue
		mConnectMap = connectMap
		mHazlecastFrontend  = instance
	}
	@Override
	public void run() {
		log.trace "Starting run()"
		try
		{
			
			while(true)
			{
				log.trace "Taking a request from ${mRequests.getName()}"
				ARequest request = mCalendarServiceQueue.take()
				log.trace "Got request: ${request.toString()}"
				handleRequest(request)
			}
			
		}
		catch (ex)
		{
			log.trace ex.toString()
		}
		
		requestsServed++
		log.trace "Exiting run()"
	}
	
	public void handleRequest(ARequest request)
	{
		log.trace "handleRequest($request)"
		switch(request)
		{
			case RetrieveEventRequest:
				doRetrieveEventRequest(request as RetrieveEventRequest)
				break;
			case ScheduleEventRequest:
				doScheduleEventRequest(request as ScheduleEventRequest)
				break;
		}
	}
	
	protected doRetrieveEventRequest(RetrieveEventRequest request)
	{
		
	}
	
	protected doScheduleEventRequest(ScheduleEventRequest request)
	{
		
	}
	
	public Integer getRequestsServed()
	{
		return requestsServed
	}
}
