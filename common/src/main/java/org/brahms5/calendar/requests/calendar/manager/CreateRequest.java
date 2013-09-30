package org.brahms5.calendar.requests.calendar.manager;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public class CreateRequest extends ARequest {

	private static final long serialVersionUID = -4706469357744631931L;
	User subjectUser = null;
	public CreateRequest(String uuid, String id, User clientUser) {
		super(uuid, id, clientUser);
	}
	public User getSubjectUser() {
		return subjectUser;
	}
	public void setSubjectUser(User subjectUser) {
		this.subjectUser = subjectUser;
	}
}
