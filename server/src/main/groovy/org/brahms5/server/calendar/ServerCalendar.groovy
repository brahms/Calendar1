package org.brahms5.server.calendar

import org.brahms5.common.TimeInterval
import org.brahms5.common.User
import org.brahms5.common.calendar.Calendar
import org.brahms5.common.calendar.Event

class ServerCalendar implements Calendar {

	List<Event> events = null;
	
	public Event retrieveEvent(User user, TimeInterval timeInterval)
	{
		return null;
	}
	
	public ServerCalendar scheduleEvent(List<User> users, Event event)
	{
		
	}

}
