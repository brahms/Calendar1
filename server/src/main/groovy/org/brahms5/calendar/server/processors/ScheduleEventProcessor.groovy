package org.brahms5.calendar.server.processors

import groovy.util.logging.Slf4j

import java.util.Map.Entry

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.User

import com.hazelcast.map.EntryBackupProcessor
import com.hazelcast.map.EntryProcessor
import org.brahms5.calendar.domain.*;

@Slf4j
public class ScheduleEventProcessor implements EntryProcessor, EntryBackupProcessor {

	final def trace = {
		str -> log.trace str
	}
	Event mEvent
	public ScheduleEventProcessor(Event event)
	{
		mEvent = event
	}
	@Override
	public void processBackup(Entry entry) {
		process(entry)
	}

	@Override
	public Object process(Entry entry) {
		final def calendar = entry.getValue() as Calendar
		final def user = new User(entry.getKey())
		
		if (event.addToCalendar(calendar)) {
			trace "Adding $mEvent to calendar owned by $user"
			entry.setValue(calendar)
		}
		else {
			trace "Cannot add $mEvent to calendar owned by $user"
		}
		
		return null
	}

	@Override
	public EntryBackupProcessor getBackupProcessor() {
		return this
	}


}
