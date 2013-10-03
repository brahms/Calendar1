package org.brahms5.calendar.requests.calendar;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public abstract class ACalendarRequest extends ARequest {

	private static final long serialVersionUID = 820625999041922943L;
	User subjectUser;

	public ACalendarRequest(String uuid, String id, User clientUser, User subjectUser) {
		super(uuid, id, clientUser);
		this.setSubjectUser(subjectUser);
	}

	public User getSubjectUser() {
		return subjectUser;
	}

	public void setSubjectUser(User subjectUser) {
		this.subjectUser = subjectUser;
	}

}
