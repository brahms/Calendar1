package org.brahms5.calendar.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Event implements Serializable, Comparable<Event>, Cloneable{
	
	public static class ConflictingEventException extends Exception
	{
		private static final long serialVersionUID = 4157523418773776285L;
		private Event conflictingEvent;
		private Event me;
		
		public Event getMe() {
			return me;
		}

		public ConflictingEventException(Event me, Event other)
		{
			this.setMe(me);
			this.setConflictingEvent(other);
		}

		private void setMe(Event me) {
			this.me = me;
			
		}

		public Event getConflictingEvent() {
			return conflictingEvent;
		}
		
		@Override
		public String toString()
		{
			return String.format("Cannot add " + getMe() + "  due to conflicting event: " + conflictingEvent.toString());
		}

		protected void setConflictingEvent(Event conflictingEvent) {
			this.conflictingEvent = conflictingEvent;
		}
	}
	protected Logger log = LoggerFactory.getLogger(getClass());
	public enum AccessControlMode implements Cloneable{
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
	User owner = new User();
	
	@Override
	public String toString()
	{
		return String.format("Event[owner: %s, access: %s description: %s timeInterval: %s]", getOwner(), getAccessControlMode(), getDescription(), getTimeInterval());
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
		if (getOwner() == null) throw new Exception("Owner is null");
	}
	@Override
	public int compareTo(Event other) {
		return getTimeInterval().compareTo(other.getTimeInterval());
	}
	public boolean canBeAccessedBy(User user) {
		switch(getAccessControlMode())
		{
		case PRIVATE:
			return user.equals(getOwner());
		case PUBLIC:
		case GROUP:
		case OPEN:
		default:
			return true;
		}
	}
	public User getOwner() 
	{
		return owner;
	}
	
	public Event setOwner(User user)
	{
		this.owner = user;
		return this;
	}
	
	public void addTo(Calendar calendar) throws Exception, ConflictingEventException
	{
		log.trace(String.format("addTo(%s) this: %s", calendar, toString()));
		if (calendar == null)
		{
			log.trace("Can't add to a null calendar");
			throw new UnsupportedOperationException();
		}
		switch(getAccessControlMode())
		{
		case PRIVATE:
		case PUBLIC:
		case OPEN:
			if (getOwner().equals(calendar.getUser()))
			{
				log.trace("Can add because owner of calendar does equal this owner");
				for (Event event : calendar.getEvents()) {
					if(this.conflictsWith(event)) {
						log.trace(String.format("Cannot add " + this.toString() + " because my time interval conflicts with event %s", event));
						throw new ConflictingEventException(this, event);
					}
				}
				log.trace("Adding.");
				calendar.getEvents().add(this);
				log.trace("Sorting.");
				Collections.sort(calendar.getEvents());
				log.trace("Done.");
				return;
			}
			else
			{	
				String error = "Cannot add because owner of calendar does not equal this owner";
				log.trace(error);
				throw new Exception(error);
				
			}
		default:
			throw new UnsupportedOperationException();
		}
		
	}

	
	@Override
	public Event clone() throws CloneNotSupportedException {
		return (Event) super.clone();
	}
	/**
	 * Allows the event to scrub itself before being sent to a given user
	 * @throws CloneNotSupportedException 
	 */
	public Event cleanFor(User user) throws CloneNotSupportedException
	{
		return clone();
	}
	public String debugString() {
		return String.format(
		"\tOwner %s\n" +
		"\tType: %s\n" +
		"\tDescription: %s\n" +
		"\tTime: %s\n", 
		(getOwner() != null) ? getOwner().getName() : "null", 
		getAccessControlMode(), 
		getDescription(),
		(getTimeInterval() != null) ? getTimeInterval().debugString() : "null");
	}
	
	public void setTimeInterval(Date start, Date end)
	{
		setTimeInterval(new TimeInterval(start, end));
	}
	
	public void setTimeInterval(Long start, Long end)
	{
		setTimeInterval(new TimeInterval(start, end));
	}
}

