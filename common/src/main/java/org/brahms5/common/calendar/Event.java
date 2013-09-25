package org.brahms5.common.calendar;


public interface Event {
	
	public Long getTimeStart();
	public Event setTimeStart(Long time);
	public Long getTimeEnd();
	public Event setTimeEnd(Long time);
	public String getDescriton();
	public Event setDescription();
	public AccessControlMode getAccessControlMode();
	public Event setAccessControlMode(AccessControlMode mode);
}
