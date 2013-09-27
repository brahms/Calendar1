package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import java.util.concurrent.Executor
import java.util.concurrent.Executors

import org.brahms5.calendar.server.db.calendar.CalendarDao
import org.brahms5.calendar.domain.Calendar

import com.hazelcast.core.EntryEvent
import com.hazelcast.core.EntryListener
import com.hazelcast.core.ILock
import com.hazelcast.core.IMap

@Slf4j
class CalendarPersistor implements EntryListener {

	CalendarDao mCalendarDao
	ILock mStartupLock
	IMap mCalendarMap
	
	public CalendarPersistor(CalendarDao dao, ILock lock, IMap calendarMap)
	{
		mCalendarDao = dao
		mStartupLock = lock
		mCalendarMap = calendarMap
		
	}
	
	public void startup()
	{
		log.trace "startup() started"
		def calendars = mCalendarDao.selectAll()
		
		log.trace "locking"
		mStartupLock.lock()
		try 
		{
			calendars.each {
				cal -> 
				def key = cal.getUser().getName()
				if (mCalendarMap.get(cal.getUser().getName()) == null)
				{
					log.trace "adding $key to CalendarMap"
					mCalendarMap.put(cal.getUser().getName(), cal)
				}
				else 
				{
					log.trace "skipping $key, already exists"
				}
			}
			log.trace "Finished adding list"
		}
		catch (ex)
		{
			log.warn "Error persisting", ex
		}
		finally
		{
			log.trace "Unlocking"
			mStartupLock.unlock()
		}
		log.trace "Adding myself as an entrylistener to ${mCalendarMap.getName()}"
		mCalendarMap.addEntryListener(this, true)
		log.trace "startup() finished"
	}
	
	
	
	
	@Override
	public void entryAdded(EntryEvent event) {
		sExecutor.execute {
			log.trace "Calendar added: ${event.key}"
			def addedCal = event.value as Calendar
			
			if (mCalendarDao.select(addedCal.getUser()) == null)
			{
				mCalendarDao.insert(addedCal)
			}
			log.trace "Finished add"
		}
		
	}

	@Override
	public void entryRemoved(EntryEvent event) {
		log.warn "HUH? Calendar removed: ${event.key}"
		
	}

	@Override
	public void entryUpdated(EntryEvent event) {
		sExecutor.execute {
			log.trace "Calendar update: ${event.key}"
			def updatedCal = event.value as Calendar
			
			if (mCalendarDao.select(updatedCal.getUser()) == null)
			{
				mCalendarDao.insert(updatedCal)
			}
			else
			{
				mCalendarDao.update(updatedCal)
			}
			log.trace "Finished update"
		}
		
	}

	@Override
	public void entryEvicted(EntryEvent event) {
		log.warn "HUH? Calendar evicted: ${event.key}"
		
	}
	
	private static final Executor sExecutor = Executors.newSingleThreadExecutor(); 

}
