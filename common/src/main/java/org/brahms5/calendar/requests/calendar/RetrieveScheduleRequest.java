package org.brahms5.calendar.requests.calendar;

import org.brahms5.calendar.domain.TimeInterval;
import org.brahms5.calendar.domain.User;

public class RetrieveScheduleRequest extends ACalendarRequest{

	User subjectUser = null;
	TimeInterval timeInterval = null;
	public RetrieveScheduleRequest(String uuid, String id, User user, User subjectUser) {
		super(uuid, id, user, subjectUser);
		
	}

	private static final long serialVersionUID = -5118945354057789434L;


	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

	public User getSubjectUser() {
		return subjectUser;
	}

	public void setSubjectUser(User user) {
		this.subjectUser = user;
	}

}
