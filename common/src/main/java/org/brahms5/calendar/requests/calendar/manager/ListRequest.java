package org.brahms5.calendar.requests.calendar.manager;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public class ListRequest extends ARequest {
	private static final long serialVersionUID = -4184654132103964718L;

	public ListRequest(String uuid, String id, User clientUser) {
		super(uuid, id, clientUser);
	}

}
