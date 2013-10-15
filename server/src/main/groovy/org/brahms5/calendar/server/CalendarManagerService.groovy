package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit

import org.brahms5.calendar.domain.Calendar
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.requests.ARequest
import org.brahms5.calendar.requests.calendar.manager.*
import org.brahms5.calendar.responses.Response
import org.brahms5.calendar.responses.calendar.manager.ConnectResponse
import org.brahms5.calendar.responses.calendar.manager.CreateResponse
import org.brahms5.calendar.responses.calendar.manager.ListResponse

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue

@Slf4j
public class CalendarManagerService implements Runnable{
	IQueue mCalendarManagerServiceQueue
	IMap mCalendarMap
	IMap mConnectionMap
	HazelcastInstance mFrontend
	Integer requestsServed = 0
	final def trace = {
		str -> log.trace str
	}
	public CalendarManagerService(IQueue queue, IMap calendarMap, HazelcastInstance instance, IMap connectionMap)
	{
		mCalendarManagerServiceQueue = queue
		mCalendarMap = calendarMap
		mFrontend = instance
		mConnectionMap = connectionMap
		trace "Constructor"
	}

	@Override
	public void run() {
		trace "Starting run()"
		try
		{
			
			while(true)
			{
				trace "Taking a request from ${mCalendarManagerServiceQueue.getName()}"
				ARequest request = mCalendarManagerServiceQueue.take()
				trace "Got request: ${request.toString()}"
				handleRequest(request)
			}
			
		}
		catch (ex)
		{
			trace ex.toString()
		}
		trace "Exiting run()"
	}
	
	void handleRequest(ARequest request)
	{
		trace "handleRequest(${request.toString()})"
		switch (request)
		{
			case ListRequest:
				trace "Got a ListRequest request"
				doList(request)
				break
			case ConnectRequest:
				trace "Got a ConnectRequest"
				doConnect(request)
				break
			case CreateRequest:
				trace "Got a CreateRequest"
				doCreate(request)
				break
			case DisconnectRequest:
				trace "Got a DisconnectRequest"
				doDisconnect(request)
				break
			case RetrieveCalendarRequest:
				trace "Got a retrieve calendar request"
				doRetrieveCalendar(request)
			default:
				trace "Unknown Request"
				break
		}
		requestsServed++
		trace "handleRequest - $requestsServed request served"
	}
	
	void doCreate(CreateRequest request)
	{
		trace "doCreate($request)"
		trace "Response will be on: " + request.getQueue()
		
		final def answerQueue = mFrontend.getQueue(request.getQueue())
		final def user = request.getSubjectUser() ?: request.getClientUser()
		mCalendarMap.lock(user.getName())
		final def cal = mCalendarMap.get(user.getName())
		try
		{
			if (cal == null) {
				cal = new Calendar()
				cal.setUser(user)
				trace "Creating calendar: ${user.getName()}"
				mCalendarMap.put(user.getName(), cal)
				offer(new CreateResponse(request.getId(), null), answerQueue)
			}
			else {
				trace "Already exists: ${user.getName()}"
				offer(new CreateResponse(request.getId(), "${user.getName()}'s calendar already exists"), answerQueue)
			}
		}
		finally
		{
			mCalendarMap.unlock(user.getName())
		}
	}
	void doConnect(ConnectRequest request)
	{
		trace "doConnect($request)"
		trace "Response will be on: " + request.getQueue()
		final def answerQueue = mFrontend.getQueue(request.getQueue())
		def cal = mCalendarMap.get(request.getSubjectUser().getName())
		if (cal == null) {
			offer(new ConnectResponse(request.getId(), "Doesn't exist"), answerQueue)
		}
		else {
			offer(new ConnectResponse(request.getId(), null), answerQueue)
		}
	}
	void doList(ListRequest request)
	{
		trace "doList($request)"
		trace "Response will be on: " + request.getQueue()
		final def answerQueue = mFrontend.getQueue(request.getQueue())
		final List<User> users = mCalendarMap.keySet().asList().sort().collect({
			name -> 
				return new User(name)
		})
		final Response response = new ListResponse(request.getId(), users);
		offer(response, answerQueue)
		
	}
	
	void offer(Response response, IQueue answerQueue)
	{
		def offered = answerQueue.offer(response, 5, TimeUnit.SECONDS)
		if (offered) {
			trace "Response was offered"
		}
		else
		{
			trace "Response didn't go through"
		}
	}
	void doDisconnect(DisconnectRequest request)
	{
		trace "doDisconnet($request)"
	}
	public Integer getRequestsServed()
	{
		return requestsServed
	}
	
	
}
