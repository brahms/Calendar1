package org.brahms5.calendar.responses.calendar.manager;

import org.brahms5.calendar.responses.Response;

public class ConnectResponse extends Response {

	private static final long serialVersionUID = -2560300411380831336L;

	public ConnectResponse(String id, String error) {
		super(id, error);
	}
	public ConnectResponse(String id){
		this(id, null);
	}

}
