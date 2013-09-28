package org.brahms5.calendar.domain;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = -2579703484224102994L;
	
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return String.format("User[name: %s]", getName());
	}
	
	public User()
	{
		
	}
	
	public User(String name)
	{
		this.name = name;
	}
}
