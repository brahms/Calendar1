package org.brahms5.calendar.domain;

import static org.junit.Assert.*;
import groovy.time.TimeCategory;

import org.brahms5.calendar.domain.Event.AccessControlMode;
import org.junit.Before;
import org.junit.Test;

class EventTest {
	Event mTestEvent
	Event mContainedEvent
	Event mConflictEventBefore
	Event mConflictEventAfter
	Event mContainingEvent
	Event mPrivateEvent
	Event mRandomEvent
	User mOwner = new User("cbrahms")
	User mRandom = new User("random")
	
	@Before
	public void setUp() throws Exception {
		use (TimeCategory) {
			mTestEvent = new Event()
			mTestEvent.setAccessControlMode(AccessControlMode.PUBLIC)
			mTestEvent.setDescription("Descripting stuff")
			mTestEvent.setTimeInterval(1.day.ago, 1.second.ago)
			mTestEvent.setOwner(mOwner)
			
			mContainedEvent = new Event()
			mContainedEvent.setAccessControlMode(AccessControlMode.PUBLIC)
			mContainedEvent.setDescription("Contained event")
			mContainedEvent.setOwner(mOwner)
			mContainedEvent.setTimeInterval(12.hours.ago, 1.hour.ago)
			
			mContainingEvent = mContainedEvent.clone()
			mContainingEvent.setTimeInterval(2.days.ago, 1.days.from.now)
			
			mConflictEventBefore = new Event()
			mConflictEventBefore.setAccessControlMode(AccessControlMode.PUBLIC)
			mConflictEventBefore.setDescription("Conflicting event")
			mConflictEventBefore.setOwner(mOwner)
			mConflictEventBefore.setTimeInterval(2.days.ago, 6.hours.ago)
			
			mConflictEventAfter = mConflictEventBefore.clone()
			mConflictEventAfter.setTimeInterval(12.hours.ago, 1.hour.from.now)
			
			mPrivateEvent = mTestEvent.clone()
			mPrivateEvent.setAccessControlMode(AccessControlMode.PRIVATE)
			
			mRandomEvent = new Event()
			mRandomEvent.setAccessControlMode(AccessControlMode.PUBLIC)
			mRandomEvent.setTimeInterval(3.days.from.now, 4.days.from.now)
			mRandomEvent.setOwner(mRandom)
		}
	}

	@Test
	public void testConflictsWith() {
		assert mTestEvent.conflictsWith(mConflictEventAfter)
		assert mTestEvent.conflictsWith(mConflictEventBefore)
		assert mTestEvent.conflictsWith(mContainedEvent)
		assert mTestEvent.conflictsWith(mContainingEvent)
		assert mTestEvent.conflictsWith(mTestEvent)
		
		
		assert mConflictEventAfter.conflictsWith(mTestEvent)
		assert mConflictEventBefore.conflictsWith(mTestEvent)
		assert mContainedEvent.conflictsWith(mTestEvent)
		assert mContainingEvent.conflictsWith(mTestEvent)
		assert mTestEvent.conflictsWith(mTestEvent)
		
	}

	@Test
	public void testContains() {
		assert mTestEvent.contains(mContainedEvent)
		assert !mContainedEvent.contains(mTestEvent)
	}

	@Test
	public void testValidate() {
		mTestEvent.validate()
	}

	@Test
	public void testCanBeAccessedBy() {
		assert mTestEvent.canBeAccessedBy(mOwner) 
		assert mTestEvent.canBeAccessedBy(mRandom)
		assert mPrivateEvent.canBeAccessedBy(mOwner)
		assert !mPrivateEvent.canBeAccessedBy(mRandom)
		
	}

	@Test
	public void testAddTo() {
		def cal = new Calendar()
		cal.setUser(mOwner)
		
		assert mTestEvent.addTo(cal)
		assert !mTestEvent.addTo(cal)
		assert !mRandomEvent.addTo(cal)
		assert !mConflictEventAfter.addTo(cal)
		assert !mConflictEventBefore.addTo(cal)
		assert !mContainedEvent.addTo(cal)
	}

}
