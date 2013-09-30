package org.brahms5.calendar.responses;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Response implements Serializable {

	String id = null;
	String error = null;
	public Response(String id, String error)
	{
		this.id = id;
		this.error = error;
	}
	@Override
	public String toString()
	{
		return String.format("%s[id: %s error: %s]", getClass().getSimpleName(), id, error);
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
