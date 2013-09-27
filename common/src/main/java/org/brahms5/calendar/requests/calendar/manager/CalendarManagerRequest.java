package org.brahms5.calendar.requests.calendar.manager;

import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.requests.ARequest;

public class CalendarManagerRequest extends ARequest{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7238910510901541552L;
	enum Type {
		CONNECT,
		LIST,
		CREATE
	}

	Type type;
	String username;

	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public CalendarManagerRequest(String uuid, String id, Type type)
	{
		super(uuid, id);
		this.type = type;
	}
	public CalendarManagerRequest(String uuid, String id, Type type, String username)
	{
		this(uuid, id, type);
		this.username = username;
	}
	public CalendarManagerRequest(String uuid, String id, Type type, User user)
	{
		this(uuid, id, type, user.getName());
	}
	@Override
	public String toString()
	{
		return String.format("CalendarManagerRequest[uuid: %s id: %s type: %s username: %s", getUuid(), getId(), getType(), getUsername());
	}
}
