package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit

import org.brahms5.calendar.domain.User
import org.brahms5.calendar.requests.calendar.manager.CalendarManagerRequest
import org.brahms5.calendar.responses.AResponse;
import org.brahms5.calendar.responses.calendar.manager.ConnectResponse
import org.brahms5.calendar.responses.calendar.manager.CreateResponse
import org.brahms5.calendar.responses.calendar.manager.ListResponse
import org.brahms5.calendar.server.db.calendar.CalendarDao;
import org.brahms5.calendar.server.processors.CreateCalendarProcessor
import org.brahms5.calendar.domain.Calendar

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue

@Slf4j
public class CalendarManager implements Runnable{
	IQueue mRequests
	IMap mCalendarMap
	IMap mConnectMap
	HazelcastInstance mFrontend
	Integer requestsServed = 0
	public boolean isShutdown = false
	public CalendarManager(IQueue queue, IMap calendarMap, HazelcastInstance instance, IMap connectMap)
	{
		mRequests = queue
		mCalendarMap = calendarMap
		mFrontend = instance
		mConnectMap = connectMap
		log.trace "Constructor"
	}

	@Override
	public void run() {
		log.trace "Starting run()"
		try
		{
			
			while(true)
			{
				log.trace "Taking a request from ${mRequests.getName()}"
				CalendarManagerRequest request = mRequests.take()
				log.trace "Got request: ${request.toString()}"
				handleRequest(request)
			}
			
		}
		catch (ex)
		{
			log.trace ex.toString()
		}
		log.trace "Exiting run()"
	}
	
	protected void handleRequest(CalendarManagerRequest request)
	{
		log.trace "handleRequest(${request.toString()})"
		switch (request.getType())
		{
			case Request.Type.LIST:
				log.trace "Got a ListRequest request"
				doList(request)
				break
			case Request.Type.CONNECT:
				log.trace "Got a ConnectRequest"
				doConnect(request)
				break
			case Request.Type.CREATE:
				log.trace "Got a CreateRequest"
				doCreate(request)
				break
		}
		requestsServed++
		log.trace "handleRequest - $requestsServed request served"
	}
	
	protected void doCreate(CalendarManagerRequest request)
	{
		log.trace "doCreate($request)"
		log.trace "Response will be on: " + request.getQueue()
		
		final def answerQueue = mFrontend.getQueue(request.getQueue())
		User user = new User()
		user.setName(request.getUsername())
		mCalendarMap.lock(user.getName())
		def cal = mCalendarMap.get(user.getName())
		try
		{
			if (cal == null) {
				cal = new Calendar()
				cal.setUser(user)
				log.trace "Creating calendar: ${user.getName()}"
				mCalendarMap.put(user.getName(), cal)
				offer(new CreateResponse(request.getId(), null), answerQueue)
			}
			else {
				log.trace "Already exists: ${user.getName()}"
				offer(new CreateResponse(request.getId(), "The calendar already exists"), answerQueue)
			}
		}
		finally
		{
			mCalendarMap.unlock(user.getName())
		}
	}
	protected void doConnect(CalendarManagerRequest request)
	{
		log.trace "doConnect($request)"
		log.trace "Response will be on: " + request.getQueue()
		final def answerQueue = mFrontend.getQueue(request.getQueue())
		def cal = mCalendarMap.get(request.getUsername())
		if (cal == null) {
			offer(new ConnectResponse(request.getId(), "Doesn't exist"), answerQueue)
		}
		else {
			mConnectMap.put(request.getUuid(), request.getUsername())
			offer(new ConnectResponse(request.getId(), null), answerQueue)
		}
	}
	protected void doList(CalendarManagerRequest request)
	{
		log.trace "doList($request)"
		log.trace "Response will be on: " + request.getQueue()
		final def answerQueue = mFrontend.getQueue(request.getQueue())
		final List<User> users = mCalendarMap.keySet().asList().sort().collect({
			name -> 
				def user = new User()
				user.setName(name)
		})
		final AResponse response = new ListResponse(request.getId(), users);
		offer(response, answerQueue)
		
	}
	
	protected void offer(AResponse response, IQueue answerQueue)
	{
		def offered = answerQueue.offer(response, 5, TimeUnit.SECONDS)
		if (offered) {
			log.trace "Response was offered"
		}
		else
		{
			log.trace "Response didn't go through"
		}
	}
	
	public Integer getRequestsServed()
	{
		return requestsServed
	}
	
	
}
