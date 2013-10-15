package org.brahms5.calendar.domain;

import static org.junit.Assert.*
import groovy.time.TimeCategory

import org.junit.Test

class GroupEventTest extends GroovyTestCase{

	@Test
	public void testAddTo() {
		use(TimeCategory) {
			final def owner = new User("cbrahms")
			def users = ["u1", "u2", "u3", "u4", "u5"].collect {
				return new User(it)
			}.toSet()
			def cal = new Calendar()
			cal.setUser(owner)
			
			def open = new OpenEvent()
			open.setDescription("Some public event")
			open.setOwner(owner)
			open.setTimeInterval(1.days.ago, 1.second.ago)
			open.addTo(cal)
			
			def gevents = [ [3.hours.ago, 2.hours.ago], [5.hours.ago, 4.hours.ago], [10.hours.ago, 9.hours.ago] ].collect { 
				array ->
					def g = new GroupEvent()
					g.setOwner(owner)
					g.setTimeInterval(array[0], array[1])
					g.setDescription("G Event")
					g.setMembers(users)
					return g
			}
			
			gevents.each {
				it.addTo(cal)
			}
			shouldFail {
				gevents[0].addTo(cal)
			}
			assert open.getEvents().size() == gevents.size()
			gevents.each {
				assert it.parent.equals(open)
			}
		}
		
		
	}

	@Test
	public void testCleanForUser() {
		use(TimeCategory) {
			final def owner = new User("cbrahms")
			final def random = new User("random")
			def users = ["cbrahms", "u1", "u2", "u3", "u4", "u5"].collect {
				return new User(it)
			}.toSet()
			def o = new OpenEvent()
			
			GroupEvent g = new GroupEvent()
			g.setOwner(owner)
			g.setTimeInterval(1.days.ago, 1.second.ago)
			g.setDescription("G Event")
			g.setMembers(users)
			g.setParent(o)
			g.validate()
			
			assert g.getMembers().size() == users.size()
			assert g.getMembers() == users
			assert g.getMembers().is(users)
			assert g.getMembers().contains(owner)
			
			GroupEvent cleaned = g.cleanFor(random)
			
			
			assert g.getMembers().is(users)
			assert g.getMembers() == users
			assert g.getMembers().size() == users.size()
			assert !g.getDescription().isEmpty()
			
			
			assert !g.is(cleaned)
			
			assert cleaned.getMembers().size() == 0
			assert cleaned.getDescription().isEmpty()
		}
	}

}
