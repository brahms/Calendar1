package org.brahms5.calendar.responses.calendar.manager;

import java.util.List;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.responses.Response;

public class ListResponse extends Response {

	private static final long serialVersionUID = 6087807606048543945L;
	List<User> users;
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public ListResponse(String id, List<User> users) {
		super(id, null);
		this.users = users;
	}
}
