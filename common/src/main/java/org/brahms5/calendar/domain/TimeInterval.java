package org.brahms5.calendar.domain;

import java.io.Serializable;

public class TimeInterval implements Serializable, Comparable<TimeInterval>{
	private static final long serialVersionUID = 759086343646516787L;
	Long timeStart = null;
	Long timeEnd = null;
	public Long getTimeStart() {
		// TODO Auto-generated method stub
		return timeStart;
	}
	public TimeInterval setTimeStart(Long time) {
		timeStart = time;
		return this;
	}
	public Long getTimeEnd() {
		return timeEnd;
	}
	public TimeInterval setTimeEnd(Long time) {
		timeEnd = time;
		return this;
	}
	
	boolean contains(TimeInterval other)
	{
		return 	other.getTimeStart() > getTimeStart() &&
				other.getTimeEnd() < getTimeEnd();
		
		
	}
	
	boolean intersects(TimeInterval other)
	{
		return (getTimeEnd() > other.getTimeStart() && getTimeStart() < other.getTimeStart()) ||
				(getTimeEnd() > other.getTimeEnd() && getTimeStart() < other.getTimeStart());
	}
	@Override
	public String toString()
	{
		return String.format("TimeInterval[timeStart: %s timeEnd: %s]", getTimeStart(), getTimeEnd());
	}
	@Override
	public int compareTo(TimeInterval other) {
		if(getTimeStart() == other.getTimeStart()) {
			if(getTimeEnd() == other.getTimeEnd()) return 0;
			if(getTimeEnd() > other.getTimeEnd()) return -1;
			else return 1;
		}
		if(getTimeStart() < other.getTimeStart()) return -1;
		else return 1;
	}
}
