package org.brahms5.calendar.domain;

import java.io.Serializable;

public class User implements Serializable, Comparable<User>, Cloneable{
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
		return getName();
	}
	
	public User()
	{
		
	}
	
	public User(String name)
	{
		this.name = name;
	}
	
	@Override 
	public boolean equals(Object other)
	{
		return (other instanceof User && 
				getName().equals(((User) other).getName()));
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	public User clone() throws CloneNotSupportedException
	{
		return (User) super.clone();
	}

	@Override
	public int compareTo(User other) {
		return getName().compareTo(other.getName());
	}
}
