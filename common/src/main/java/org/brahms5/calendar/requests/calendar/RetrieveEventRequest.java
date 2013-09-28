package org.brahms5.calendar.requests.calendar;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public class RetrieveEventRequest extends ARequest{

	public RetrieveEventRequest(String uuid, String id, User user) {
		super(uuid, id, user);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -5118945354057789434L;

}
