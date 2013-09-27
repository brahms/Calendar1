package org.brahms5.calendar.requests;

import java.io.Serializable;

@SuppressWarnings("serial")
abstract public class ARequest implements Serializable {
	
	String uuid;
	String id;
	
	public ARequest(String uuid, String id)
	{
		this.uuid = uuid;
		this.id = id;
	}
	
	public String getQueue() 
	{
		return this.uuid + ".answer";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String toString()
	{
		return String.format("%s[uuid: %s id: %s]", getClass().getSimpleName(), getUuid(), getId());
	}

}
