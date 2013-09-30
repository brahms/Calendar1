package org.brahms5.calendar.client

import java.util.concurrent.TimeUnit;

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.requests.ARequest
import org.brahms5.calendar.requests.calendar.RetrieveScheduleRequest
import org.brahms5.calendar.requests.calendar.ScheduleEventRequest
import org.brahms5.calendar.requests.calendar.manager.*
import org.brahms5.calendar.responses.Response
import org.brahms5.calendar.responses.calendar.manager.ConnectResponse
import org.brahms5.commons.Constants

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IQueue
import com.hazelcast.core.IdGenerator
import com.hazelcast.core.MultiMap
import com.lightspeedworks.events.EventEmitter


@Slf4j
public class Client extends EventEmitter
{	
	HazelcastInstance mHazlecast = null;
	String mUuid
	IQueue mAnswerQueue
	MultiMap mUserMap
	IQueue mCalendarManagerQueue
	IQueue mCalendarServiceQueue
	String mAnswerQueueName
	User mClientUser
	
	final def trace = {
		str -> log.trace str
	}
	public Client(String username)
	{
		mClientUser = new User(username)
		trace "Constructor"
	}
	
	public connect()
	{
		this.emit("connect", null);
		def cfg = new ClientConfig();
		cfg.getGroupConfig().setName(Constants.CLUSTER_FRONTEND)
		mHazlecast = HazelcastClient.newHazelcastClient(cfg);
		IdGenerator idGenerator = mHazlecast.getIdGenerator(Constants.IDS_CLIENT);
		mUuid = "client-" + Long.toHexString(idGenerator.newId())
		mAnswerQueueName = mUuid + ".answer"
		
		mAnswerQueue = mHazlecast.getQueue(mAnswerQueueName);
		mCalendarManagerQueue = mHazlecast.getQueue(Constants.QUEUE_CALENDAR_MANAGER)
		mCalendarServiceQueue = mHazlecast.getQueue(Constants.QUEUE_CALENDAR_SERVICE)
		mUserMap = mHazlecast.getMultiMap(Constants.MAP_USERS)
		mUserMap.put(mClientUser.getName(), mAnswerQueueName)
		log.info "Answer queue is: $mAnswerQueueName"
	}
	
	public shutdown()
	{
		disconnectCalendar()
		try
		{	
			log.info "Destroying answer queue: ${mAnswerQueueName}"
			mAnswerQueue?.destroy()
		}
		catch(ex)
		{
			log.warn "Error destroying queue", ex.toString()
		}
		try
		{
			trace "Destroy multi map record for ${mClientUser.getName()}, ${mAnswerQueueName}"
			boolean b = mUserMap?.remove(mClientUser.getName(), mAnswerQueueName)
			if (b) {
				trace "Removed"
			}
			else {
				trace "Failed to remove"
			}
		}
		catch (ex)
		{
			log.warn ("Error removing multimap entry", ex)
		}
		mHazlecast?.getLifecycleService()?.shutdown()
	}
	
	public getLoggedInUsers()
	{
		try
		{
			return mUserMap.keySet()
		}
		catch (ex)
		{
			return []
		}
	}
	
	public connectCalendar(String username)
	{
		trace("connectCalendar($username)")
		User user = new User()
		user.setName(username)
		
		trace("Clearing answer queue: $mAnswerQueueName")
		mAnswerQueue.clear();

		final def req = new ConnectRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		req.setSubjectUser(user)
		ConnectResponse resp = sendRequest(req, mCalendarManagerQueue)
		trace("Received response: $resp")
		return resp.getError()
	}
	
	public String createCalendar(String username)
	{
		trace("connectCalendar($username)")
		User user = new User()
		user.setName(username)
		
		trace("Clearing answer queue: $mAnswerQueueName")
		mAnswerQueue.clear();

		final def req = new CreateRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		req.setSubjectUser(new User(username))
		Response resp = sendRequest(req, mCalendarManagerQueue)
		trace("Received response: $resp")
		return resp.getError() ?: "Success!"
		
	}
	
	public List<User> listCalendars()
	{
		trace "listCalendars()"
		
		final def req = new ListRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		
		Response resp = sendRequest(req, mCalendarManagerQueue)
		trace("Received response: $resp")
		return resp.getError() ?: "Success!"
	}
	
	public List<Event> retrieveSchedule(String username, TimeInterval timeInterval)
	{
		trace "retrieveSchedule($username, $timeInterval)"
		def req = new RetrieveScheduleRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		
		req.setSubjectUser(username ? new User(username) : null )
		req.setTimeInterval(timeInterval)
		
		Response resp = sendRequest(req, mCalendarServiceQueue)
		trace("Received response: $resp")
		return resp.getError() ?: "Success!"
	}
	public String scheduleEvent(List<String> users, Event event)
	{
		trace "scheduleEvent($users, $event)"
		def req = new ScheduleEventRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		
		req.setUserList(users)
		req.setEvent(event)
		
		Response resp = sendRequest(req, mCalendarServiceQueue)
		trace("Received response: $resp")
		return resp.getError() ?: "Success!"
		
	}
	
	public void disconnectCalendar()
	{
		
		try
		{
			log.info "Disconnecting from any Calendars"
			mCalendarManagerQueue.offer(new DisconnectRequest(mUuid, UUID.randomUUID().toString(), mClientUser))
		}
		catch(ex)
		{
			log.warn "Error disconnecting from calendar", ex
		}
	}
	
	Response sendRequest(ARequest req, IQueue queue)
	{
		trace("Created request with request client id of ${req.getUuid()}")
		trace("Created request with request id of ${req.getId()}")
		trace("Offering request to queue: " + queue.getName())
		queue.offer(req)
		trace("Request taken by: ${queue.getName()}")
		trace("Taking response from ${mAnswerQueue.getName()}...")
		Response resp = mAnswerQueue.poll(2, TimeUnit.SECONDS)
		
		if (resp == null) {
			log.warn("Timed out waiting for a response on ${mAnswerQueue.getName()}")
			throw new Exception("Response timed out")
		}
		trace("Received $resp")
		return resp
	}
	
}
