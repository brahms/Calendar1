package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import org.brahms5.calendar.domain.Calendar
import org.brahms5.calendar.domain.CalendarEvent
import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.TimeInterval
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.requests.ARequest
import org.brahms5.calendar.requests.calendar.RetrieveScheduleRequest
import org.brahms5.calendar.requests.calendar.ScheduleEventRequest
import org.brahms5.calendar.requests.calendar.manager.*
import org.brahms5.calendar.responses.Response
import org.brahms5.calendar.responses.calendar.manager.ListResponse
import org.brahms5.calendar.responses.calendar.manager.RetrieveCalendarResponse
import org.brahms5.calendar.responses.calendars.RetrieveScheduleResponse
import org.brahms5.calendar.responses.calendars.ScheduleEventResponse
import org.brahms5.commons.Constants

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IQueue
import com.hazelcast.core.IdGenerator
import com.hazelcast.core.LifecycleEvent
import com.hazelcast.core.LifecycleListener
import com.hazelcast.core.Message
import com.hazelcast.core.MessageListener
import com.hazelcast.core.MultiMap
import com.hazelcast.core.LifecycleEvent.LifecycleState


@Slf4j
public class Client implements MessageListener<CalendarEvent>, LifecycleListener
{	
	/** This is the magic sauce that makes our communication with the server possible **/
	HazelcastInstance mHazlecast = null;
	
	/** This is our unique id for the client **/
	String mUuid
	
	/** This is our answer queue's, where we get responses from the server **/
	IQueue mAnswerQueue
	
	/** This is the currently logged in users **/
	MultiMap mUserMap
	
	/** This is the calendar manager queue, where we send calendar manager requests **/
	IQueue mCalendarManagerQueue
	
	/** This is the calendar service queue, where we send calendar service requests **/
	IQueue mCalendarServiceQueue
	
	/** This is our answer queue's name, where we get responses from the server **/
	String mAnswerQueueName
	
	/**	This is the client's user **/
	User mClientUser
	
	/** This is the connected calendars user **/
	User mSubjectUser
	
	/** When we login, we grab our own calendar, we keep it synced to the persistent version
	 * and alert ourselves to any appointments
	 */
	Calendar clientCalendar
	
	/** This tells us if the client calendar's is dirty, since
	 * it gets updated on a different thread, we make it atomic **/
	AtomicBoolean mClientIsDirty = new AtomicBoolean(false)
	
	AppointmentAlerter mAlerter = new AppointmentAlerter(this);
	
	String mLifecycleRegistrationString = null
	
	boolean mIsShutdown = false
	
	final def trace = {
		str -> log.trace "${getClientUser()}: $str"
	}
	
	final def warn = {
		str -> log.warn "${getClientUser()}: $str"
	}
	
	
	/**
	 * Creates a Client, which is used to connect to the server
	 * @param username
	 */
	public Client(String username)
	{
		mClientUser = new User(username)
		trace "Constructor"
		println "Ready"
	}
	
	
	/** Connects to the hazelcast frontend cluster **/
	public connect(boolean medusa)
	{
		def cfg = new ClientConfig();
		cfg.getGroupConfig().setName(Constants.CLUSTER_FRONTEND)
		cfg.getProperties().put("hazelcast.logging.type", "slf4j")
		
		if (medusa)
		{
			println "Using TCP/IP cluster configuration for medusa"
			(1..19).each {
				def addr = "compute-0-${it}.local:5701"
				println "\tAdding $addr to config"
				cfg.addAddress(addr)
			}
		}
		
		println "Logging in."
		mHazlecast = HazelcastClient.newHazelcastClient(cfg);
		mLifecycleRegistrationString = mHazlecast.getLifecycleService().addLifecycleListener(this);
		
		//
		// Generate the unique identifier for our client
		//
		IdGenerator idGenerator = mHazlecast.getIdGenerator(Constants.IDS_CLIENT);
		mUuid = "client-" + Long.toHexString(idGenerator.newId())
		mAnswerQueueName = mUuid + ".answer"
		
		// Get some of our standard distributed structures
		mAnswerQueue = mHazlecast.getQueue(mAnswerQueueName);
		mCalendarManagerQueue = mHazlecast.getQueue(Constants.QUEUE_CALENDAR_MANAGER)
		mCalendarServiceQueue = mHazlecast.getQueue(Constants.QUEUE_CALENDAR_SERVICE)
		mHazlecast.getTopic(Constants.TOPIC_CALENDAR_EVENTS).addMessageListener(this)
		mUserMap = mHazlecast.getMultiMap(Constants.MAP_USERS)
		mUserMap.put(mClientUser.getName(), mAnswerQueueName)
		
		log.info "Answer queue is: $mAnswerQueueName"
		
		def user = listCalendars().find {
			return mClientUser.equals(it)
		}
		
		if (user != null)
		{
			trace "Starting Alerter"
			mAlerter.start();
		}
		else
		{
			println "$mClientUser doesn't have an existing calendar, you should create one!"
		}
	}
	
	/**
	 * Shuts down all our hazelcast stuff
	 * @return
	 */
	public synchronized shutdown()
	{
		log.trace "Trying to shutdown()"
		if (mHazlecast != null && mIsShutdown == false)
		{
			
			try
			{
				log.info "Shutting down lifecycle listener"
				mHazlecast.getLifecycleService().removeLifecycleListener(mLifecycleRegistrationString);
			}
			catch (ex)
			{
				
			}
			try
			{
				log.info "Shutting down alerter thread."
				
				if (mAlerter?.isAlive()) mAlerter.interrupt();
			}
			catch(ex)
			{
				
			}
			try
			{
				log.info "Destroying answer queue: ${mAnswerQueueName}"
				mAnswerQueue?.destroy()
			}
			catch(ex)
			{
			}
			try
			{
				log.info "Removing myself from the topic"
				mHazlecast.getTopic(Constants.TOPIC_CALENDAR_EVENTS).removeMessageListener(this);
			}
			catch(ex)
			{
				
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
				
			}
			mHazlecast?.getLifecycleService()?.shutdown()
			mHazlecast = null;
		}
		else
		{
			log.trace "Already shutdown"
		}
	}
	
	/**
	 * Returns all the currently logged in users
	 * @return
	 */
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
	
	
	/**
	 * Creates a calendar for a given user
	 * 
	 * Uses the CalendarManagerService
	 * @param username
	 * @return
	 */
	public String createCalendar(String username)
	{
		trace("connectCalendar($username)")
		User user = new User()
		user.setName(username)
		

		final def req = new CreateRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		req.setSubjectUser(new User(username))
		Response resp = sendRequest(req, mCalendarManagerQueue)
		
		if (mAlerter != null && mAlerter.isAlive() == false && mClientUser.equals(req.getSubjectUser())) {
			trace "Starting my own appointment alerter"
			mAlerter.start();
		}
		
		return "Success"
		
	}
	
	public String dump()
	{
		if (mAlerter.isAlive()) {
			return getClientCalendar().debugString();
		}
	}
	
	/**
	 * Lists all the calendars in the cluster
	 * 
	 * Uses the CalendarManager service
	 * @return
	 */
	public List<User> listCalendars()
	{
		trace "listCalendars()"
		
		final def req = new ListRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		
		ListResponse resp = (ListResponse) sendRequest(req, mCalendarManagerQueue)
		trace("Received response: $resp")
		return resp.getUsers();
	}
	
	/**
	 * Retrieves the schedule for a given user (PUBLIC, Private, and GROUP events)
	 * @param username
	 * @param timeInterval
	 * @return
	 */
	public List<Event> retrieveSchedule(String username, TimeInterval timeInterval)
	{
		trace "retrieveSchedule($username, $timeInterval)"
		def req = new RetrieveScheduleRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
	
		req.setSubjectUser(username ? new User(username) : null )
		req.setTimeInterval(timeInterval)
		req.validate()
		RetrieveScheduleResponse resp = (RetrieveScheduleResponse) sendRequest(req, mCalendarServiceQueue)

		return resp.getEvents();
	}
	
	/**
	 * Schedules an event
	 * @param event
	 * @return
	 */
	public String scheduleEvent(Event event)
	{
		trace "scheduleEvent($event)"
		def req = new ScheduleEventRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		
		req.setEvent(event)
		
		ScheduleEventResponse resp = sendRequest(req, mCalendarServiceQueue)
		trace("Received response: $resp")
		return  "Success!"
		
	}
	
	/**
	 * Schedules an event for a specific user's calendar (this can only be group events)
	 * @param user
	 * @param event
	 * @return
	 */
	public String scheduleEvent(User user, Event event)
	{
		trace "scheduleEvent($event)"
		def req = new ScheduleEventRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		
		req.setEvent(event)
		
		ScheduleEventResponse resp = sendRequest(req, mCalendarServiceQueue)
		trace("Received response: $resp")
		return "Success!"
		
	}
	
	
	synchronized Response sendRequest(ARequest req, IQueue queue) throws Exception
	{
		trace("Created request with request client id of ${req.getUuid()}")
		trace("Created request with request id of ${req.getId()}")
		trace("Offering request to queue: " + queue.getName())
		queue.offer(req)
		trace("Request taken by: ${queue.getName()}")
		trace "Clearing answer queue"
		trace("Taking response from ${mAnswerQueue.getName()}...")
		Response resp = mAnswerQueue.poll(15, TimeUnit.SECONDS)
		
		if (resp == null) {
			log.warn("Timed out waiting for a response on ${mAnswerQueue.getName()}")
			throw new Exception("Response timed out")
		}
		if (resp.getError())
		{
			throw new Exception(resp.getError());
		}
		trace("Received $resp")
		return resp
	}

	@Override
	public void onMessage(Message<CalendarEvent> message) {
		CalendarEvent event = (CalendarEvent) message.getMessageObject()
		if (mClientUser.equals(event.getSubjectUser())) {
			trace "Client Calendar is dirty"
			println "\nYour calendar has been updated\n"
			mClientIsDirty.set(true)
		}
	}
	
	protected Calendar retrieveCalendar(User user) 
	{
		RetrieveCalendarRequest request = new RetrieveCalendarRequest(mUuid, UUID.randomUUID().toString(), mClientUser)
		
		def resp = (RetrieveCalendarResponse) sendRequest(request, mCalendarManagerQueue)
		
		if (resp.getError() != null) {
			throw new Exception(resp.getError());
		}
		
		return resp.getCalendar();
		
	}
	public Calendar getClientCalendar()
	{
		while(mClientIsDirty.getAndSet(false) || clientCalendar == null) {
			try {
				trace "retrieveCalendar"
				clientCalendar = retrieveCalendar(mClientUser)
			}
			catch (ex)
			{
				log.warn "Error trying to grab the calendar: " + ex
				clientCalendar = null;
			}
		}
		return clientCalendar
	}
	
	public User getClientUser()
	{
		return mClientUser
	}


	@Override
	public void stateChanged(LifecycleEvent event) {
		trace "Lifecycle changed: $event"
		switch(event.getState()) {
			case LifecycleState.SHUTTING_DOWN:
			case LifecycleState.SHUTDOWN:
				mIsShutdown = true;
				System.exit(1)
		}
		
	}
}
