package org.brahms5.calendar.server.processors

import groovy.util.logging.Slf4j;

import java.util.Map.Entry

import org.brahms5.calendar.domain.Calendar
import org.brahms5.calendar.domain.User

import com.hazelcast.map.EntryBackupProcessor
import com.hazelcast.map.EntryProcessor

@Slf4j
public class CreateCalendarProcessor implements EntryProcessor, EntryBackupProcessor {

	User mUser
	public CreateCalendarProcessor(User user)
	{
		mUser = user
	}
	@Override
	public Object process(Entry entry) {
		if (entry.getValue() == null)
		{
			log.trace "Created new entry"
			Calendar calendar = new Calendar()
			calendar.setUser(mUser)
			entry.setValue(calendar)
			
			return calendar
		}
		else
		{
			log.trace "Didn't create entry"
			return null
		}
		
	}

	@Override
	public EntryBackupProcessor getBackupProcessor() {
		return this
	}

	@Override
	public void processBackup(Entry entry) {
		// TODO Auto-generated method stub
		
	}

}
