package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.requests.calendar.manager.CalendarManagerRequest
import org.brahms5.calendar.requests.calendar.manager.CalendarManagerRequest.Type
import org.brahms5.calendar.responses.calendar.manager.ConnectResponse
import org.brahms5.calendar.responses.calendar.manager.CreateResponse
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
	Long mUuid
	IQueue mAnswerQueue
	MultiMap mUserMap
	IQueue mCalendarManagerQueue
	String mAnswerQueueName
	String mUsername
	public Client(String username)
	{
		mUsername = username
		log.trace "Constructor"
	}
	
	public connect()
	{
		this.emit("connect", null);
		def cfg = new ClientConfig();
		cfg.getGroupConfig().setName(Constants.CLUSTER_FRONTEND)
		mHazlecast = HazelcastClient.newHazelcastClient(cfg);
		IdGenerator idGenerator = mHazlecast.getIdGenerator(Constants.IDS_CLIENT);
		mUuid = "client-" + Long.toHexString(idGenerator.getId())
		mAnswerQueueName = mUuid + ".answer"
		
		mAnswerQueue = mHazlecast.getQueue(mAnswerQueueName);
		mCalendarManagerQueue = mHazlecast.getQueue(Constants.QUEUE_CALENDAR_MANAGER)
		mUserMap = mHazlecast.getMultiMap(Constants.MAP_USERS)
		mUserMap.put(mUsername, mAnswerQueueName)
		log.info "Answer queue is: $mAnswerQueueName"
	}
	
	public shutdown()
	{
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
			log.trace "Destroy multi map record for ${mUsername}, ${mAnswerQueueName}"
			boolean b = mUserMap?.remove(mUsername, mAnswerQueueName)
			if (b) {
				log.trace "Removed"
			}
			else {
				log.trace "Failed to remove"
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
		log.trace("connectCalendar($username)")
		User user = new User()
		user.setName(username)
		
		log.trace("Clearing answer queue: $mAnswerQueueName")
		mAnswerQueue.clear();

		final def req = new CalendarManagerRequest(mAnswerQueueName, UUID.randomUUID().toString(), Type.CONNECT, user)
		log.trace("Created request with uuid of ${req.getId()}")
		log.trace("Offering request...")
		mCalendarManagerQueue.offer(req)
		log.trace("Request taken")
		log.trace("Taking response...")
		ConnectResponse resp = mAnswerQueue.take()
		log.trace("Received response: $resp")
	}
	
	public String createCalendar(String username)
	{
		log.trace("connectCalendar($username)")
		User user = new User()
		user.setName(username)
		
		log.trace("Clearing answer queue: $mAnswerQueueName")
		mAnswerQueue.clear();

		final def req = new CalendarManagerRequest(mAnswerQueueName, UUID.randomUUID().toString(), Type.CREATE, user)
		log.trace("Created request with uuid of ${req.getId()}")
		log.trace("Offering request to: " + mCalendarManagerQueue.getName())
		mCalendarManagerQueue.offer(req)
		log.trace("Request taken")
		log.trace("Taking response...")
		CreateResponse resp = mAnswerQueue.take()
		log.trace("Received response: $resp")
		return resp.getError() ?: "Success!"
		
	}
	
	public Event retrieveEvent(String username, TimeInterval timeInterval)
	{
		
	}
	public Event retrieveEvent(TimeInterval timeInterval)
	{
		
	}
	
	public String scheduleEvent(List<String> users, Event event)
	{
		
	}
	
}
