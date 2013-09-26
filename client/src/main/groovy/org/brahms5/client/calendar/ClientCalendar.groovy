package org.brahms5.client.calendar

import org.brahms5.client.Client
import org.brahms5.common.TimeInterval
import org.brahms5.common.User
import org.brahms5.common.calendar.Event

import com.darylteo.rx.promises.Promise

public class ClientCalendar {
	Client mAsyncClient = null;
	
	public ClientCalendar(Client client)
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
