package org.brahms5.calendar.domain;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class TimeInterval implements Serializable, Comparable<TimeInterval>, Cloneable{
	private static final long serialVersionUID = 759086343646516787L;
	Long timeStart = null;
	Long timeEnd = null;
	static DateTimeFormatter sDateFormatter = DateTimeFormat.forPattern("M/dd/yyyy hh:mm:ss a");
	static PeriodFormatter sPeriodFormatter = new PeriodFormatterBuilder()
    .appendYears().appendSuffix(" years ")
    .appendMonths().appendSuffix(" months ")
    .appendWeeks().appendSuffix(" weeks ")
    .appendDays().appendSuffix(" days ")
    .appendHours().appendSuffix(" hours ")
    .appendMinutes().appendSuffix(" minutes ")
    .appendSeconds().appendSuffix(" seconds")
    .printZeroNever()
    .toFormatter();

	public Long getTimeStart() {
		// TODO Auto-generated method stub
		return timeStart;
	}
	public TimeInterval setTimeStart(Long time) {
		timeStart = time;
		return this;
	}
	public TimeInterval setTimeStart(Date time) {
		timeStart = time.getTime();
		return this;
	}
	public Long getTimeEnd() {
		return timeEnd;
	}
	public TimeInterval setTimeEnd(Long time) {
		timeEnd = time;
		return this;
	}
	public TimeInterval setTimeEnd(Date time) {
		timeEnd = time.getTime();
		return this;
	}
	
	public TimeInterval() {}
	public TimeInterval(Long start, Long end) {timeStart = start; timeEnd = end;}
	public TimeInterval(Date start, Date end) {this(start.getTime(), end.getTime());}
	
	boolean contains(TimeInterval other)
	{
		return 	other.getTimeStart() > getTimeStart() &&
				other.getTimeEnd() < getTimeEnd();
	}
	
	boolean intersects(TimeInterval other)
	{
		return (getTimeEnd() >= other.getTimeStart() && getTimeStart() <= other.getTimeStart()) ||
				(getTimeEnd() >= other.getTimeEnd() && getTimeStart() <= other.getTimeEnd()) || 
				contains(other) ||
				other.contains(this) || 
				equals(other);
	}
	@Override
	public String toString()
	{
		return String.format("TimeInterval[timeStart: %s, timeEnd: %s, interval: %s]", 
				(getTimeStart() == null) ? "null" :  sDateFormatter.print(getTimeStart()), 
			    (getTimeEnd() == null) ? "null" : sDateFormatter.print(getTimeEnd()),
				(getInterval() == null ? "null" : sPeriodFormatter.print(getInterval().toPeriod())));
	}
	
	public String debugString()
	{
		return String.format("%s to %s (%s)", 
				(getTimeStart() == null) ? "null" :  sDateFormatter.print(getTimeStart()), 
				(getTimeEnd() == null) ? "null" :sDateFormatter.print(getTimeEnd()),
				(getInterval() == null ? "null" : sPeriodFormatter.print(getInterval().toPeriod())));
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
	
	@Override
	public boolean equals(Object object) {
		TimeInterval other = (TimeInterval) object;
		return other != null && getTimeEnd() == other.getTimeEnd() && getTimeStart() == other.getTimeStart();
	}
	public TimeInterval clone() throws CloneNotSupportedException
	{
		return (TimeInterval) super.clone();
	}
	
	public Interval getInterval()
	{
		return (null != getTimeEnd() && null != getTimeStart()) ? new Interval(getTimeStart(), getTimeEnd()) : null;
	}
	public boolean containsStartTime(Long time) {
		return getTimeStart() <= time && getTimeEnd() >= time; 
	}
}
