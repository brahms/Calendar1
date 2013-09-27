package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import org.brahms5.calendar.server.db.calendar.CalendarDao
import org.brahms5.calendar.server.db.calendar.ICalendarDao
import org.brahms5.commons.Constants
import org.springframework.jdbc.datasource.DriverManagerDataSource

import com.hazelcast.config.Config
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MapConfig.InMemoryFormat
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.core.IQueue


@Slf4j
class Server {
	HazelcastInstance mHazlecastFrontend
	HazelcastInstance mHazlecastBackend

	IQueue mCalendarManagerQueue
	IMap mCalendarMap
	IMap mConnectMap
	CalendarManager mCalendarManager
	Thread mCalendarManagerThread
	CalendarPersistor mCalendarPersistor

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

		log.trace "Getting CalendarManagerQueue"
		mCalendarManagerQueue = mHazlecastFrontend.getQueue(Constants.QUEUE_CALENDAR_MANAGER)

		log.trace "Getting CalendarMap"
		mCalendarMap = mHazlecastBackend.getMap(Constants.MAP_CALENDARS)
		
		log.trace "Getting ConnectMap"
		mConnectMap = mHazlecastBackend.getMap(Constants.MAP_CONNECT)
		
		log.trace "Creating the CalendarManager"
		mCalendarManager = new CalendarManager(mCalendarManagerQueue, mCalendarMap, mHazlecastFrontend, mConnectMap)
		

		log.trace "Starting the persistor"
		mCalendarPersistor = new CalendarPersistor(mCalendarDao, mHazlecastBackend.getLock(Constants.LOCK_STARTUP), mCalendarMap)
		mCalendarPersistor.startup()
		
		log.trace "Starting CalendarManager thread"
		mCalendarManagerThread = new Thread(mCalendarManager)
		mCalendarManagerThread.setDaemon(true)
		mCalendarManagerThread.start()

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

		return """CalendarManager Status: ${mCalendarManagerThread.isAlive()}
CalendarManager Requests Served: ${mCalendarManager.getRequestsServed()}
Calendars in DB: ${mCalendarDao.count()}
Calendars in CalendarMap:
${builder.toString()}
Clients logged in:
${builder2.toString()}
"""
	}
}
