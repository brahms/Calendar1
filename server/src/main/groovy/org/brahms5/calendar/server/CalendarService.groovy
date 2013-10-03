package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit

import org.brahms5.calendar.domain.*
import org.brahms5.calendar.domain.Event.AccessControlMode
import org.brahms5.calendar.requests.calendar.*
import org.brahms5.calendar.responses.Response
import org.brahms5.calendar.responses.calendars.RetrieveScheduleResponse
import org.brahms5.calendar.server.processors.ScheduleEventProcessor

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ILock
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue
@Slf4j
class CalendarService implements Runnable {
	IQueue mCalendarServiceQueue
	IMap mCalendarMap
	IMap mConnectMap
	HazelcastInstance mHazlecastFrontend
	Integer requestsServed = 0
	ILock mCalendarGlobalLock
	
	final def trace = 
	{
		str -> log.trace str
	}
	public CalendarService(IQueue serviceQueue, IMap calendarMap, IMap connectMap, HazelcastInstance instance, ILock globalLock)
	{
		mCalendarMap = calendarMap
		mCalendarServiceQueue = serviceQueue
		mConnectMap = connectMap
		mHazlecastFrontend  = instance
		mCalendarGlobalLock = globalLock
	}
	@Override
	public void run() {
		trace "Starting run()"
		try
		{
			
			while(true)
			{
				trace "Taking a request from ${mCalendarServiceQueue.getName()}"
				ACalendarRequest request = mCalendarServiceQueue.take()
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
	
	void handleRequest(ACalendarRequest request)
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
		final User clientUser = request.getClientUser()
		final User subjectUser = request.getSubjectUser()
		final Calendar calendar = getCalendar(clientUser, subjectUser)
		final TimeInterval timeInterval = request.getTimeInterval()
		RetrieveScheduleResponse response = null
		if (calendar == null)  {
			 response = new RetrieveScheduleResponse(request.getId(), null)
		}
		else {
			response = new RetrieveScheduleResponse(request.getId(), "Calendar doesn't exist.")
			response.setEvents(calendar.getEvents(clientUser, timeInterval))
		}
		
		offer(response, request.getQueue())
	}
	
	void doScheduleEventRequest(ScheduleEventRequest request)
	{
		trace "doScheduleEventRequest($request)"
		try
		{
			trace "Valiating event."
			final def event = request.getEvent()
			if (event.getOwner() != request.getClientUser()) throw new Exception("Owner doesn't match client")
			event.validate()
			
			trace "Event validated."
			trace "Creating processor"
			trace "locking"
			mCalendarGlobalLock.lock();
			trace "locked"
			switch (event.getAccessControlMode()) 
			{
				case AccessControlMode.PUBLIC:
				case AccessControlMode.PRIVATE:
				case AccessControlMode.OPEN:
					def cal = mCalendarMap.get(event.getOwner().getName())
					trace "Trying to add to $cal"
					def added = event.addTo(cal)
					trace ("Added: $added")
					mCalendarMap.replace(event.getOwner().getName(), cal)
					trace "Updated map"
					break;
				case AccessControlMode.GROUP:
					def groupEvent = event as GroupEvent
					trace "Group event"
					def results = mCalendarMap.getAll(groupEvent.getMembers().toSet()).collect({
						trace "Trying to add to $it."
						def added = groupEvent.addTo(it)
						trace "Did we actually add: $added."
						mCalendarMap.replace(it.getKey(), cal)
						trace "Updated map."
						
						return added
					})
					
					def addedAtAll = results.findAll()
					
					trace "Tried to add to ${results.size()} calendars. Actually added to ${addedAtAll}.size() calendars"
					break;
			}
			trace "unlocking"
			mCalendarGlobalLock.unlock();
			trace "unlocked"
			offer(new Response(request.getId(), null), request.getQueue())
		}
		catch(ex)
		{
			log.warn "Unable to schedule event", ex.toString()
			try {
				trace "Unlocking global lock after exception"
				mCalendarGlobalLock.unlock()
			} catch(ex2){}
			offer(new Response(request.getId(), ex.toString()), request.getQueue())
		}
	}
	
	Calendar getCalendar(ACalendarRequest request)
	{
		trace "getCalendar()";
		def user = request.getSubjectUser();
		
		if (user == null) {
			trace "Subject user is null, using client user"
			 user = request.getSubjectUser();
		}
		
		trace "getting calendar for $user"
		
		def cal =  mCalendarMap.get(user.getName())
		
		if (cal == null) {
			log.warn "Calendar is null for $user";
		}
		
		return cal;
		
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
