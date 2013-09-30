package org.brahms5.calendar.domain;

import java.io.Serializable;


public class Event implements Serializable, Comparable<Event>{
	public enum AccessControlMode{
		PRIVATE,
		PUBLIC,
		GROUP,
		OPEN;
	}

	private static final long serialVersionUID = 682860590633116264L;
	
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
	TimeInterval timeInterval = new TimeInterval();
	String description = "No Description";
	AccessControlMode accessControlMode = AccessControlMode.PUBLIC;
	String uuid = null;
	User owner = null;
	
	@Override
	public String toString()
	{
		return String.format("Event[access: %s description: %s timeInterval: %s]", getAccessControlMode(), getDescription(), getTimeInterval());
	}
	
	boolean conflictsWith(Event other)
	{
		return getTimeInterval().intersects(other.getTimeInterval());
	}
	
	boolean contains(Event other)
	{
		return getTimeInterval().contains(other.getTimeInterval());
	}
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}
	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	void validate() throws Exception
	{
		if (getTimeInterval() == null) throw new Exception("TimeInterval");
		if (getTimeInterval().getTimeEnd() == null) throw new Exception("TimeEnd is null");
		if (getTimeInterval().getTimeStart() == null) throw new Exception("TimeStart is null");
		if (getTimeInterval().getTimeEnd() <= getTimeInterval().getTimeStart()) throw new Exception("TimeEnd <= TimeStart");
		if (getDescription() == null) throw new Exception("Description is null");
		if (getAccessControlMode() == null) throw new Exception("AccessControl is null");
	}
	@Override
	public int compareTo(Event other) {
		return getTimeInterval().compareTo(other.getTimeInterval());
	}
}

