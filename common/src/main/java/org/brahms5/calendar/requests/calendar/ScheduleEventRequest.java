package org.brahms5.calendar.requests.calendar;

import java.util.ArrayList;
import java.util.List;

import org.brahms5.calendar.domain.Event;
import org.brahms5.calendar.domain.User;

public class ScheduleEventRequest extends ACalendarRequest {
	List<User> userList = new ArrayList<User>();
	Event event = null;
	public ScheduleEventRequest(String uuid, String id, User user, User subjectUser) {
		super(uuid, id, user, subjectUser);
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	private static final long serialVersionUID = 1077771650478136988L;

}
