package org.brahms5.calendar.domain

import groovy.time.TimeCategory;

import org.junit.After
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.brahms5.calendar.domain.Calendar
import org.brahms5.calendar.domain.Event.AccessControlMode;
public class CalendarTest {
	Calendar mCalendar = null
	User mUser = new User("cbrahms")
	
	@Before void setUp() {
		mCalendar = new Calendar()
		mCalendar.setUser(mUser)
	}
	
	@After void tearDown() {
		
	}
	
	@Test public void testAddEvent() {
		use(TimeCategory) {
			Event e = new Event()
			e.setAccessControlMode(AccessControlMode.PUBLIC)
			e.setOwner(mUser)
			e.setDescription("Test")
			e.setTimeInterval(new TimeInterval(1.days.ago, 1.second.ago));
			e.validate()
			e.addTo(mCalendar)
			
			assert mCalendar.getEvents().size() == 1
			
			
			e = new Event()
			e.setAccessControlMode(AccessControlMode.PUBLIC)
			e.setOwner(mUser)
			e.setDescription("Test")
			e.setTimeInterval(new TimeInterval(1.days.ago, 1.second.ago));
			e.validate()
			
			try {
				e.addTo(mCalendar)
				assertTrue false, "Should cause an exception when adding a conflicting event"
			}
			catch (ex)
			{
				assert mCalendar.getEvents().size() == 1
			}
		}
	}
	
	@Test public void testRetrieveEvents()
	{
		
		use(TimeCategory) {
			Event e = new Event()
			e.setAccessControlMode(AccessControlMode.PUBLIC)
			e.setOwner(mUser)
			e.setDescription("Test")
			e.setTimeInterval(new TimeInterval(1.days.ago, 1.second.ago));
			e.validate()
			e.addTo(mCalendar)
			
			List<Event> events = mCalendar.getEvents(mUser, new TimeInterval(2.days.ago, 2.days.from.now));
			assert events.size() == 1;
			
			e = new Event()
			e.setAccessControlMode(AccessControlMode.OPEN)
			e.setOwner(mUser)
			e.setDescription("Test")
			e.setTimeInterval(new TimeInterval(1.days.ago, 1.second.ago));
			e.validate()
			e.addTo(mCalendar)
			
		}
	}
	
	
	
}
