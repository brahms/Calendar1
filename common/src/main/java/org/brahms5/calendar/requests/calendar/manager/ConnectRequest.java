package org.brahms5.calendar.requests.calendar.manager;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public class ConnectRequest extends ARequest {

	User subjectUser = null;
	private static final long serialVersionUID = 7226288730134294500L;

	public ConnectRequest(String uuid, String id, User user) {
		super(uuid, id, user);
	}

	public User getSubjectUser() {
		return subjectUser;
	}

	public void setSubjectUser(User subjectUser) {
		this.subjectUser = subjectUser;
	}
	
}
