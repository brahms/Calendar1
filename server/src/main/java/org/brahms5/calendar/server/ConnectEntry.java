package org.brahms5.calendar.server;

import java.io.Serializable;

import org.brahms5.calendar.domain.User;

public class ConnectEntry implements Serializable {

	private static final long serialVersionUID = 5746502908184398038L;
	
	User clientUser = null;
	User connectedUser = null;
	public User getClientUser() {
		return clientUser;
	}
	public void setClientUser(User clientUser) {
		this.clientUser = clientUser;
	}
	public User getConnectedUser() {
		return connectedUser;
	}
	public void setConnectedUser(User connectedUser) {
		this.connectedUser = connectedUser;
	}

}
