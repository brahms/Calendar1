appender("FILE", FileAppender) {
	file = "server.log"
	append = true
	encoder(PatternLayoutEncoder) {
	  pattern = "%d{HH:mm:ss} [%thread] %-5level %logger{5} - %msg%n"
	}
  }

root(DEBUG, ["FILE"])


logger("org.brahms5", TRACE, ["FILE"], false)


