package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit

import org.brahms5.calendar.requests.ARequest
import org.brahms5.calendar.requests.calendar.*
import org.brahms5.calendar.responses.Response

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue

@Slf4j
class CalendarService implements Runnable {
	IQueue mCalendarServiceQueue
	IMap mCalendarMap
	IMap mConnectMap
	HazelcastInstance mHazlecastFrontend
	Integer requestsServed = 0
	
	final def trace = 
	{
		str -> log.trace str
	}
	public CalendarService(IQueue serviceQueue, IMap calendarMap, IMap connectMap, HazelcastInstance instance)
	{
		mCalendarMap = calendarMap
		mCalendarServiceQueue = serviceQueue
		mConnectMap = connectMap
		mHazlecastFrontend  = instance
	}
	@Override
	public void run() {
		trace "Starting run()"
		try
		{
			
			while(true)
			{
				trace "Taking a request from ${mCalendarServiceQueue.getName()}"
				ARequest request = mCalendarServiceQueue.take()
				trace "Got request: ${request.toString()}"
				handleRequest(request)
			}
			
		}
		catch (ex)
		{
			trace ex.toString()
		}
		
		requestsServed++
		trace "Exiting run()"
	}
	
	public void handleRequest(ARequest request)
	{
		trace "handleRequest($request)"
		switch(request)
		{
			case RetrieveScheduleRequest:
				doRetrieveScheduleRequest(request as RetrieveScheduleRequest)
				break;
			case ScheduleEventRequest:
				doScheduleEventRequest(request as ScheduleEventRequest)
				break;
			default:
				trace "Unknown Request"
		}
	}
	
	void doRetrieveScheduleRequest(RetrieveScheduleRequest request)
	{
		
	}
	
	void doScheduleEventRequest(ScheduleEventRequest request)
	{
		trace "doScheduleEventRequest($request)"
		try
		{
			trace "Valiating event."
			def event = request.getEvent()
			event.validate()
			
			trace "Event validated."
			
			final def userList = request.getUserList()
			
			Set<String> names = new TreeSet()
			userList.each {
				user -> names.add(user.getName())
			}
			
			
			
			
		}
		catch(ex)
		{
			log.warn "Unable to schedule event: " + ex.toString()
		}
	}
	
	void offer(Response response, String answerQueueName)
	{
		trace "Will offer response to: $answerQueueName"
		def queue = mHazlecastFrontend.getQueue(answerQueueName)
		if (queue == null) {
			trace "$answerQueueName came back as null"
		}
		trace "Offering to: ${queue.getName()}"
		def offered = queue.offer(response, 5, TimeUnit.SECONDS)
		if (offered) {
			trace "Response was offered"
		}
		else
		{
			trace "Response didn't go through"
		}
	}
	public Integer getRequestsServed()
	{
		return requestsServed
	}
}
