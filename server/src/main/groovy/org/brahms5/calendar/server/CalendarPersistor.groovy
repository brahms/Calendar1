package org.brahms5.calendar.server

import groovy.util.logging.Slf4j

import java.util.concurrent.Executor
import java.util.concurrent.Executors

import org.brahms5.calendar.domain.Calendar
import org.brahms5.calendar.server.db.calendar.CalendarDao
import org.h2.engine.User

import com.hazelcast.core.EntryEvent
import com.hazelcast.core.EntryListener
import com.hazelcast.core.ILock
import com.hazelcast.core.IMap

@Slf4j
class CalendarPersistor implements EntryListener {

	CalendarDao mCalendarDao
	ILock mStartupLock
	IMap mCalendarMap
	def trace = { str ->
		log.trace str
	}
	
	public CalendarPersistor(CalendarDao dao, ILock lock, IMap calendarMap)
	{
		mCalendarDao = dao
		mStartupLock = lock
		mCalendarMap = calendarMap
		
	}
	
	public void startup()
	{
		trace "startup() started"
		
		trace "Selected all calendars"
		final def calendars = mCalendarDao.selectAll()
		
		trace "Creating lookup table of calendar names for later"
		final TreeSet names = new TreeSet()
		calendars.each {
			names.add(it.getUser().getName())
		}
		
		trace "locking"
		mStartupLock.lock()
		try 
		{
			
			// for every calendar, see if we need to add a value
			trace "Checking for any entries that don't exist in the map"
			calendars.each { cal -> 
				def key = cal.getUser().getName()
				if (mCalendarMap.get(cal.getUser().getName()) == null)
				{
					trace "adding $key to CalendarMap"
					mCalendarMap.put(cal.getUser().getName(), cal)
				}
				else 
				{
					trace "skipping $key, already exists"
				}
			}
			calendars = null
			
			trace "Finished adding list"
		}
		catch (ex)
		{
			log.warn "Error persisting", ex
		}
		finally
		{
			trace "Unlocking"
			mStartupLock.unlock()
			
			trace "Adding myself as an entrylistener to ${mCalendarMap.getName()}"
			mCalendarMap.addEntryListener(this, true)
			
			// now pickup any old values we are missing, we'll catch newer ones after they get added via our listner
			trace "Checking for any new entries"
			mCalendarMap.keySet().each { name ->
				if (!names.contains(name)) {
					trace "Inserting $name, didn't exist in local db."
					try {
						mCalendarDao.insert(mCalendarMap.get(name))
					}
					catch(ex) {
						log.warn "Error inserting $name", ex
					}
				}
			}
		}
		trace "startup() finished"
	}
	
	
	
	
	@Override
	public void entryAdded(EntryEvent event) {
		sExecutor.execute {
			trace "Calendar added: ${event.key}"
			def addedCal = event.value as Calendar
			
			if (mCalendarDao.select(addedCal.getUser()) == null)
			{
				mCalendarDao.insert(addedCal)
			}
			trace "Finished add"
		}
		
	}

	@Override
	public void entryRemoved(EntryEvent event) {
		log.warn "HUH? Calendar removed: ${event.key}"
		
	}

	@Override
	public void entryUpdated(EntryEvent event) {
		sExecutor.execute {
			trace "Calendar update: ${event.key}"
			def updatedCal = event.value as Calendar
			
			if (mCalendarDao.select(updatedCal.getUser()) == null)
			{
				mCalendarDao.insert(updatedCal)
			}
			else
			{
				mCalendarDao.update(updatedCal)
			}
			trace "Finished update"
		}
		
	}

	@Override
	public void entryEvicted(EntryEvent event) {
		log.warn "HUH? Calendar evicted: ${event.key}"
		
	}
	
	private static final Executor sExecutor = Executors.newSingleThreadExecutor(); 

}
