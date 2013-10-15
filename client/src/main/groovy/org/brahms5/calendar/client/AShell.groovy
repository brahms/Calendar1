package org.brahms5.calendar.client

import groovy.util.logging.Slf4j

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder
import org.joda.time.format.DateTimeParser

@Slf4j
abstract class AShell {
	DateTimeParser[] parsers = [
		DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm" ).getParser(),
		DateTimeFormat.forPattern( "yyyy-MM-dd" ).getParser(),
		DateTimeFormat.forPattern( "MM/dd/yy" ).getParser(),
		DateTimeFormat.forPattern( "MM/dd/yy HH:mm" ).getParser(),
		DateTimeFormat.forPattern( "yyyy/MM/dd" ).getParser(),
		DateTimeFormat.forPattern( "yyyy/MM/dd HH:mm" ).getParser(),
		DateTimeFormat.forPattern( "MM/dd/yyyy" ).getParser(),
		DateTimeFormat.forPattern("MM/dd/yyyy HH:mm").getParser()
	];
	DateTimeFormatter dateParser = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();

	protected Long getTime(String timeString) {
		log.trace "Converting $timeString to date"
		if (timeString.isNumber()) {
			log.trace "It's a number."
			return Long.parseLong(timeString)
		}
		else if(timeString.contains(".")){
			def binding = new Binding();
			def sh = new GroovyShell(binding)
			def command = """\
	use(groovy.time.TimeCategory) {
	   return ${timeString}
	}"""			
			log.trace "Using command: $command"
			try {
				Date date =  sh.evaluate(command) as Date
				log.trace "Evaluated: $date"
				Long time = date.getTime();
				log.trace "Returning $time"
				return time
			}
			catch(ex) {
				log.warn "Can't parse the command."
				return null
			}
		}
		else {
			try {
				log.trace "Parsing using joda time"
				DateTime dateTime = dateParser.parseDateTime(timeString);
				log.trace "Parsed datetime: $dateTime"
				return dateTime.toInstant().getMillis()
			}
			catch (ex) {
				log.warn "Bad command."
				println "Error can't parse: $timeString: $ex"
				return null
			}
		}
	}
	
	protected AAShell()
	{
		
	}
}
