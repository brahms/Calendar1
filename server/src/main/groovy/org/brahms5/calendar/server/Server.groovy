package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import org.brahms5.calendar.domain.Calendar
import org.brahms5.calendar.server.db.calendar.CalendarDao
import org.brahms5.calendar.server.db.calendar.ICalendarDao
import org.brahms5.commons.Constants
import org.springframework.jdbc.datasource.DriverManagerDataSource

import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MapConfig.InMemoryFormat
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ILock
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue
import com.hazelcast.core.ITopic


@Slf4j
class Server {
	HazelcastInstance mHazlecastFrontend
	HazelcastInstance mHazlecastBackend

	IQueue mCalendarManagerQueue
	IQueue mCalendarServiceQueue
	IMap mCalendarMap
	IMap mConnectionMap
	ITopic mCalendarEvents
	ILock mCalendarGlobalLock
	CalendarManagerService mCalendarManager
	Thread mCalendarManagerThread
	CalendarPersistor mCalendarPersistor
	
	Thread mCalendarServiceThread
	CalendarService mCalendarService

	ICalendarDao mCalendarDao
	public Server() {
		log.trace "Constructor"

		log.trace "Getting frontend cluster instance"
		def frontendConfig = new Config();
		frontendConfig.getGroupConfig().setName(Constants.CLUSTER_FRONTEND)
		mHazlecastFrontend = Hazelcast.newHazelcastInstance(frontendConfig);

		log.trace "Getting backend cluster instance"
		def backendConfig = new Config()
		backendConfig.getGroupConfig().setName(Constants.CLUSTER_BACKEND)
		def calendarMapConfig = new MapConfig()
		calendarMapConfig.setName(Constants.MAP_CALENDARS)
		calendarMapConfig.setInMemoryFormat(InMemoryFormat.OBJECT)
		backendConfig.addMapConfig(calendarMapConfig)
		mHazlecastBackend = Hazelcast.newHazelcastInstance(backendConfig)


		log.trace "Creating database"
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:calendars");
		dataSource.setUsername("");
		dataSource.setPassword("");

		log.trace "Creating CalendarDao"
		mCalendarDao = new CalendarDao()
		mCalendarDao.setDataSource(dataSource);
		
		log.trace "Getting the global calendar lock"
		mCalendarGlobalLock = mHazlecastBackend.getLock(Constants.LOCK_GLOBAL)
		
		log.trace "Getting CalendarManagerQueue"
		mCalendarManagerQueue = mHazlecastFrontend.getQueue(Constants.QUEUE_CALENDAR_MANAGER)
		
		log.trace "Getting CalendarServiceQueue"
		mCalendarServiceQueue = mHazlecastFrontend.getQueue(Constants.QUEUE_CALENDAR_SERVICE)

		log.trace "Getting CalendarMap"
		mCalendarMap = mHazlecastBackend.getMap(Constants.MAP_CALENDARS)
		
		log.trace "Getting ConnectionMap"
		mConnectionMap = mHazlecastBackend.getMap(Constants.MAP_CONNECTIONS)
		
		log.trace "Getting the Calendar Events Topic"
		mCalendarEvents  = mHazlecastFrontend.getTopic(Constants.TOPIC_CALENDAR_EVENTS)
		
		log.trace "Creating the CalendarManager"
		mCalendarManager = new CalendarManagerService(mCalendarManagerQueue, mCalendarMap, mHazlecastFrontend, mConnectionMap)
		
		log.trace "Creating the CalendarService"
		mCalendarService = new CalendarService(mCalendarServiceQueue, mCalendarMap, mConnectionMap, mHazlecastFrontend, mCalendarGlobalLock, mCalendarEvents)

		log.trace "Starting the CalendarPersistor"
		mCalendarPersistor = new CalendarPersistor(mCalendarDao, mCalendarGlobalLock, mCalendarMap)
		mCalendarPersistor.startup()
		
		log.trace "Starting CalendarManager thread"
		mCalendarManagerThread = new Thread(mCalendarManager)
		mCalendarManagerThread.setDaemon(true)
		mCalendarManagerThread.start()
		
		log.trace "Starting the CalendarService thread"
		mCalendarServiceThread = new Thread(mCalendarService)
		mCalendarServiceThread.setDaemon(true)
		mCalendarServiceThread.start()
		

	}

	public shutdown() {
		log.trace "shutdown()"
		mCalendarManagerThread?.interrupt()
		mHazlecastFrontend?.getLifecycleService()?.shutdown()
		mHazlecastBackend?.getLifecycleService()?.shutdown()
	}

	public String getStatus()
	{
		def builder = new StringBuilder()
		mCalendarMap.keySet().each {
			if (mCalendarMap.get(it) != null) builder.append("\t${it}\n")
		}

		def builder2 = new StringBuilder()
		def map = mHazlecastFrontend.getMultiMap(Constants.MAP_USERS)
		map.keySet().each {
			def uuids = map.get(it)
			for (String uuid : uuids)
			{
				builder2.append("\tUser: $it, Client Answer Queue: $uuid\n")
			}
		}
		
		def connectionBuilder = new StringBuilder()
		def connectMapEntrySet = mConnectionMap.entrySet()
		connectMapEntrySet.each {
			entry ->
				def key = entry.getKey()
				def val = entry.getValue() as ConnectionEntry
				connectionBuilder.append("\t$key -> (${val.getClientUser()} ==> ${val.getSubjectUser()})\n");
		}
		return """\
======================================================================
-------------------------STATUS---------------------------------------
======================================================================

CalendarManager Is Alive: ${mCalendarManagerThread.isAlive()}
CalendarManager Requests Served: ${mCalendarManager.getRequestsServed()}

CalendarService Is Alive: ${mCalendarServiceThread.isAlive()}
CalendarService Requests Served: ${mCalendarService.getRequestsServed()}

Calendars in DB: ${mCalendarDao.count()}

Calendars in CalendarMap (${connectMapEntrySet.size()}:
${builder.toString()}

Clients logged in:
${builder2.toString()}
======================================================================
"""
	}
	
	public Calendar getCalendar(String name)
	{
		return mCalendarMap.get(name)
	}
	
	public List<Calendar> getAllCalendars()
	{
		return mCalendarMap.values().findAll().toList() as List<Calendar>
	}
	
	public void obliterate()
	{
		mCalendarGlobalLock.lock()
		mCalendarMap.keySet().each {
			mCalendarMap.remove(it);
		}
		mCalendarGlobalLock.unlock()
	}
}
