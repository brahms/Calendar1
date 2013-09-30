package org.brahms5.calendar.requests.calendar.manager;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public class DisconnectRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6817397130075600375L;

	public DisconnectRequest(String uuid, String id, User clientUser) {
		super(uuid, id, clientUser);
	}

}
