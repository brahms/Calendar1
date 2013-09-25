package org.brahms5.common;

public class TimeIntervalImpl implements TimeInterval {
	Long timeStart = null;
	Long timeEnd = null;
	@Override
	public Long getTimeStart() {
		// TODO Auto-generated method stub
		return timeStart;
	}
	@Override
	public TimeInterval setTimeStart(Long time) {
		timeStart = time;
		return this;
	}
	@Override
	public Long getTimeEnd() {
		return timeEnd;
	}
	@Override
	public TimeInterval setTimeEnd(Long time) {
		timeEnd = time;
		return this;
	}
}
