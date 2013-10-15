package org.brahms5.calendar.requests.calendar.manager;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public class RetrieveCalendarRequest extends ARequest {
	private static final long serialVersionUID = 8014544221822304004L;

	private String subjectUser;
	public RetrieveCalendarRequest(String uuid, String id, User clientUser) {
		super(uuid, id, clientUser);
	}
	public String getSubjectUser() {
		return subjectUser;
	}
	public void setSubjectUser(String subjectUser) {
		this.subjectUser = subjectUser;
	}

}
