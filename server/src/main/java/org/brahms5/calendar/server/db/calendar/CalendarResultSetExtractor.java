package org.brahms5.calendar.server.db.calendar;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.SerializationUtils;
import org.brahms5.calendar.domain.Calendar;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CalendarResultSetExtractor implements ResultSetExtractor<Calendar> {

	  @Override
	  public Calendar extractData(ResultSet rs) throws SQLException {
	    Calendar calendar = (Calendar) SerializationUtils.deserialize(rs.getBinaryStream("blob"));
	    return calendar;
	  }
}
