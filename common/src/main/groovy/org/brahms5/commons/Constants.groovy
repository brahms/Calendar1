package org.brahms5.commons

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

public interface Constants {
	final def MAP_USERS = "userMap"
	final def MAP_CALENDARS = "calendarMap"
	final def MAP_CONNECTIONS = "connectMap"
	final def TOPIC_CALENDAR_EVENTS = "calendarEvents"
	final def QUEUE_CALENDAR_MANAGER = "calendarManagerQueue"
	final def QUEUE_CALENDAR_SERVICE = "calendarServiceQueue"
	final def IDS_CLIENT = "clientIds"
	final def LOCK_STARTUP = "startup"
	final def LOCK_GLOBAL = "calendarGlobal"
	final def CLUSTER_FRONTEND = "frontend"
	final def CLUSTER_BACKEND = "backend"
	final def CALENDAR_MANAGER_SLEEP_TIME = 3 * 1000
}
