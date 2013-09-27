package org.brahms5.calendar.domain;

import java.io.Serializable;


public class Event implements Serializable{
	public enum AccessControlMode{
		PRIVATE,
		PUBLIC,
		GROUP,
		OPEN;
	}

	private static final long serialVersionUID = 682860590633116264L;
	public Long getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(Long timeStart) {
		this.timeStart = timeStart;
	}
	public Long getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(Long timeEnd) {
		this.timeEnd = timeEnd;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public AccessControlMode getAccessControlMode() {
		return accessControlMode;
	}
	public void setAccessControlMode(AccessControlMode accessControlMode) {
		this.accessControlMode = accessControlMode;
	}
	Long timeStart;
	Long timeEnd;
	String description;
	AccessControlMode accessControlMode;
	
	@Override
	public String toString()
	{
		return String.format("Event[access: %s description: %s timeStart: %s timeEnd: %s]", getAccessControlMode(), getDescription(), getTimeStart(), getTimeEnd());
	}
}
