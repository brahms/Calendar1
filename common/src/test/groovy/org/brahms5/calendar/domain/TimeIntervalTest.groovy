package org.brahms5.calendar.domain;

import static org.junit.Assert.*;
import groovy.time.TimeCategory;

import org.junit.Before;
import org.junit.Test;

public class TimeIntervalTest {
	@Before
	public void setUp() {
		
	}
	@Test
	public void testContains() {
		use(TimeCategory) {
			assert (new TimeInterval(2.hours.ago, 2.hours.from.now).contains(
					new TimeInterval(1.hour.ago, 1.hour.from.now)))
		}
	}

	@Test
	public void testIntersects() {
		use(TimeCategory) {
			def t1 = new TimeInterval(2.hours.ago, 2.hours.from.now)
			def t2 = new TimeInterval(1.hours.ago, 3.hours.from.now)
			
			assert t1.intersects(t2)
			assert t2.intersects(t1)
		}
	}

}
