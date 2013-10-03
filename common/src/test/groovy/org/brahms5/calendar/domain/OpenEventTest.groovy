package org.brahms5.calendar.domain;

import static org.junit.Assert.*
import groovy.time.TimeCategory

import org.junit.Test

class OpenEventTest {

	@Test
	public void testConflictsWith() {
		use(TimeCategory) {
			OpenEvent e = new OpenEvent()
			e.setDescription("OpenEvent")
			e.setOwner(new User("cbrahms"))
			e.setTimeInterval(1.day.ago, 1.second.ago)
			
			OpenEvent o = new OpenEvent()
			o.setDescription("OpenEvent")
			o.setOwner(new User("cbrahms"))
			o.setTimeInterval(12.hours.ago, 1.second.ago)
			
			assert e.conflictsWith(o)
		}
		
	}

}
