package org.brahms5.calendar.responses.calendar.manager;

import java.util.List;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.responses.AResponse;

public class ListResponse extends AResponse {

	private static final long serialVersionUID = 6087807606048543945L;
	public List<User> users;
	public ListResponse(String id, String error, List<User> users) {
		super(id, error);
		this.users = users;
	}
}
