package org.brahms5.client.calendar

import com.darylteo.rx.promises.Promise

import org.brahms5.client.AsyncClient;
import org.brahms5.common.TimeInterval;
import org.brahms5.common.User
import org.brahms5.common.calendar.Event

public class ClientCalendar {
	AsyncClient mAsyncClient = null;
	
	public ClientCalendar(AsyncClient client)
	{
		mAsyncClient = client;
	}
	
	public Promise<Event> retrieveEvent(User user, TimeInterval timeInterval)
	{
		final Promise<Event> p = Promise.defer();
		
		
		return p;
	}
	
	public Promise scheduleEvent(List<User> users, Event event)
	{
		final Promise p = Promise.defer();
		
		return p;
	}
}
