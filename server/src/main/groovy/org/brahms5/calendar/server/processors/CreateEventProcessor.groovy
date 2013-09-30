package org.brahms5.calendar.server.processors

import groovy.util.logging.Slf4j

import java.util.Map.Entry

import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.User

import com.hazelcast.map.EntryBackupProcessor
import com.hazelcast.map.EntryProcessor
import org.brahms5.calendar.domain.*;

@Slf4j
public class CreateEventProcessor implements EntryProcessor, EntryBackupProcessor {

	final def trace = {
		str -> log.trace str
	}
	Event mEvent
	public CreateEventProcessor(Event event)
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
		
		calendar.getEvents().each { event ->
			event.
		}
	}

	@Override
	public EntryBackupProcessor getBackupProcessor() {
		return this
	}


}
