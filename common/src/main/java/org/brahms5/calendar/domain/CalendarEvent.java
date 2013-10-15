package org.brahms5.calendar.domain;

import java.io.Serializable;

public class CalendarEvent implements Serializable {
	private static final long serialVersionUID = 4604684896969064213L;
	private User clientUser;
	public User getClientUser() {
		return clientUser;
	}
	public void setClientUser(User clientUser) {
		this.clientUser = clientUser;
	}
	public String getClientUuid() {
		return clientUuid;
	}
	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public User getSubjectUser() {
		return subjectUser;
	}
	public void setSubjectUser(User subjectUser) {
		this.subjectUser = subjectUser;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String clientUuid;
	private Long timestamp;
	private User subjectUser;
}
