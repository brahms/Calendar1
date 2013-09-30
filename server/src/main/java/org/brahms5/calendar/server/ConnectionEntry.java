package org.brahms5.calendar.server;

import java.io.Serializable;

import org.brahms5.calendar.domain.User;

public class ConnectionEntry implements Serializable {

	private static final long serialVersionUID = 5746502908184398038L;
	
	User clientUser = null;
	User subjectUser = null;
	public User getClientUser() {
		return clientUser;
	}
	public void setClientUser(User clientUser) {
		this.clientUser = clientUser;
	}
	public User getSubjectUser() {
		return subjectUser;
	}
	public void setSubjectUser(User subjectUser) {
		this.subjectUser = subjectUser;
	}
	
	public ConnectionEntry()
	{
		
	}
	
	public ConnectionEntry(User client, User subject)
	{
		this.clientUser = client;
		this.subjectUser = subject;
	}

}
