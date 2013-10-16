package org.brahms5.calendar.server

import groovy.time.TimeCategory;
import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit

import org.brahms5.calendar.domain.*
import org.brahms5.calendar.domain.Event.AccessControlMode;
import org.brahms5.calendar.requests.calendar.*
import org.brahms5.calendar.responses.Response
import org.brahms5.calendar.responses.calendars.RetrieveScheduleResponse
import org.brahms5.calendar.responses.calendars.ScheduleEventResponse

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.HazelcastInstanceNotActiveException
import com.hazelcast.core.ILock
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue
import com.hazelcast.core.ITopic
import com.hazelcast.spi.exception.CallTimeoutException;
@Slf4j
class CalendarService implements Runnable {
	IQueue mCalendarServiceQueue
	IMap mCalendarMap
	IMap mConnectMap
	HazelcastInstance mHazlecastFrontend
	Integer requestsServed = 0
	ILock mCalendarGlobalLock
	ITopic mCalendarEvents
	
	final def trace = 
	{
		str -> log.trace str
	}
	public CalendarService(IQueue serviceQueue, IMap calendarMap, IMap connectMap, HazelcastInstance instance, ILock globalLock, ITopic calendarEvents)
	{
		mCalendarMap = calendarMap
		mCalendarServiceQueue = serviceQueue
		mConnectMap = connectMap
		mHazlecastFrontend  = instance
		mCalendarGlobalLock = globalLock
		mCalendarEvents = calendarEvents
	}
	@Override
	public void run() {
		trace "Starting run()"
		try
		{
			
			while(true)
			{
				try
				{
					
					trace "Taking a request from ${mCalendarServiceQueue.getName()}"
					ACalendarRequest request = mCalendarServiceQueue.take()
					trace "Got request: ${request.toString()}"
					handleRequest(request)
				}
				catch (HazelcastInstanceNotActiveException ex)
				{
					throw ex;
				}
				catch (InterruptedException ex)
				{
					throw ex;
				}
				catch(ex)
				{
					log.warn "Error in CalendarService", ex
				}
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
		final Calendar calendar = getCalendar(request)
		final TimeInterval timeInterval = request.getTimeInterval()
		RetrieveScheduleResponse response = null
		if (calendar == null)  {
			 response = new RetrieveScheduleResponse(request.getId(), "Calendar doesn't exist.")
		}
		else {
			response = new RetrieveScheduleResponse(request.getId(), null)
			response.setEvents(calendar.getEvents(request.getClientUser(), timeInterval))
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
			
			def error = null
			try 
			{
				switch (event.getAccessControlMode()) 
				{
					case AccessControlMode.PUBLIC:
					case AccessControlMode.PRIVATE:
					case AccessControlMode.OPEN:
						final Calendar cal = mCalendarMap.get(event.getOwner().getName())
						trace "Trying to add to $cal"
						event.addTo(cal)
						mCalendarMap.replace(event.getOwner().getName(), cal)
						trace "Updated map"
						trace "Publishing event"
						def calendarEvent = new CalendarEvent()
						calendarEvent.setClientUser(request.getClientUser())
						calendarEvent.setSubjectUser(cal.getUser())
						calendarEvent.setClientUuid(request.getUuid())
						calendarEvent.setTimestamp(System.currentTimeMillis())
						mCalendarEvents.publish(calendarEvent)
						trace "Event published"
						break;
					case AccessControlMode.GROUP:
						
						final def groupEvent = event as GroupEvent
						
						trace "Trying to add event: $groupEvent"
						
						final def keys = groupEvent.getMembers().collect({return it.getName()}).toSet()
						
						def results = mCalendarMap.getAll(keys).collect({
							final def key = it.key
							final Calendar cal = it.value
							
							trace "Trying to add to ${key}'s calendar"
							final def added = {
								try {
									groupEvent.addTo(cal)
									mCalendarMap.replace(key, cal)
									trace "Updated map."
									trace "Publishing event"
									def calendarEvent = new CalendarEvent()
									calendarEvent.setClientUser(request.getClientUser())
									calendarEvent.setSubjectUser(cal.getUser())
									calendarEvent.setClientUuid(request.getUuid())
									calendarEvent.setTimestamp(System.currentTimeMillis())
									mCalendarEvents.publish(calendarEvent)
									trace "Event published"
									return true
								}
								catch (ex) {
									log.trace "Unable to add to $key"
									error = error?:"";
									error += ex.toString() +"\n"
									return false
								}
							}.call()
							return added
						})
						
						def addedAtAll = results.findAll()
						
						trace "Tried to add to ${results.size()} calendars. Actually added to ${addedAtAll.size()} calendars"
						break;
				}
			}
			catch (ex) 
			{
				error = ex.toString();
				log.warn "Exception scheduling", ex
			}
			finally
			{
				trace "unlocking"
				mCalendarGlobalLock.unlock();
				trace "unlocked"
			}
			offer(new ScheduleEventResponse(request.getId(), error), request.getQueue())
		}
		catch(ex)
		{
			log.warn "Unable to schedule event", ex.toString()
			try {
				trace "Unlocking global lock after exception"
				mCalendarGlobalLock.unlock()
			} catch(ex2){}
			offer(new ScheduleEventResponse(request.getId(), ex.toString()), request.getQueue())
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
