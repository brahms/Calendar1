package org.brahms5.calendar.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Calendar implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(Calendar.class);
	private static final long serialVersionUID = 8954015604549311498L;
	List<Event> events = new ArrayList<Event>();

	public List<Event> getEvents() {
		return events;
	}

	/**
	 * Returns a cleaned list of events for a user given an interval
	 * 
	 * @param user
	 * @param interval
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public List<Event> getEvents(User user, TimeInterval interval) {
		List<Event> returnedEvents = new ArrayList<Event>();

		log.trace(String.format("getEvents(%s, %s)", user, interval));
		for (Event event : getEvents()) {
			log.trace(String.format("Testing %s", event));
			if (event instanceof OpenEvent) {
				log.trace(String.format("\tis OpenEvent"));
				if (interval.contains(event.getTimeInterval())) {
					log.trace(String.format("\tand is contained"));
					try {
						returnedEvents.add(event.cleanFor(user));
						log.trace(String.format("\tAdded"));
					} catch (CloneNotSupportedException e) {
					}
				} else {

					log.trace(String.format("\tNot contained"));
					for (GroupEvent groupEvent : ((OpenEvent) event)
							.getEvents()) {
						log.trace(String.format("\tTesting Group Event: %s", groupEvent));
						if (interval.contains(groupEvent.getTimeInterval())) {
							log.trace(String.format("\tContained"));
							try {
								returnedEvents.add(groupEvent.cleanFor(user));
								log.trace(String.format("\tAdded"));
							} catch (CloneNotSupportedException e) {
							}
						}
						else
						{
							log.trace(String.format("\tNot contained"));
						}
					}
				}
			} else {
				log.trace(String.format("\tIs not open event"));
				if (interval.contains(event.getTimeInterval())
						&& event.canBeAccessedBy(user)) {
					log.trace(String.format("\tIs Contained"));
					try {
						returnedEvents.add(event.cleanFor(user));
						log.trace(String.format("\tAdded"));
					} catch (CloneNotSupportedException e) {
					}
				}
				else
				{
					log.trace(String.format("\tNot contained"));
				}
			}
		}

		return returnedEvents;
	}

	public List<Event> getEventsStartingWithin(TimeInterval interval) {
		List<Event> returnedEvents = new ArrayList<Event>();

		for (Event event : getEvents()) {

			if (event instanceof OpenEvent) {
				for (GroupEvent groupEvent : ((OpenEvent) event).getEvents()) {
					if (interval.containsStartTime(groupEvent.getTimeInterval()
							.getTimeStart())) {
						returnedEvents.add(groupEvent);
					}
				}
			} else {
				if (interval.containsStartTime(event.getTimeInterval()
						.getTimeStart())) {
					returnedEvents.add(event);
				}
			}
		}

		return returnedEvents;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	User user;

	@Override
	public String toString() {
		return String.format("Calendar[user: %s (%s open, %s other)]",
				getUser(), getOpenEvents().size(), getNonOpenEvents().size());
	}

	public String debugString() {
		StringBuilder b = new StringBuilder();

		b.append(String.format(
				"--------%s's Calendar (%s open, %s other)--------\n",
				getUser().getName(), getOpenEvents().size(), getNonOpenEvents()
						.size()));
		for (Event event : getEvents()) {
			b.append(String.format("\n------------------------------------\n"));
			b.append(event.debugString());
			b.append(String.format("\n------------------------------------\n"));
		}

		return b.toString();
	}

	private List<Event> getNonOpenEvents() {
		List<Event> events = new ArrayList<Event>();
		for (Event event : getEvents()) {
			if (event instanceof OpenEvent) {
				events.addAll(((OpenEvent) event).getEvents());
			} else {
				events.add(event);
			}
		}

		return events;
	}

	private List<Event> getOpenEvents() {
		List<Event> events = new ArrayList<Event>();
		for (Event event : getEvents()) {
			if (event instanceof OpenEvent)
				events.add(event);
		}
		return events;
	}

}
