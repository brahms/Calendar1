package org.brahms5.calendar.domain;

import java.io.Serializable;

public class TimeInterval implements Serializable{
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
}
