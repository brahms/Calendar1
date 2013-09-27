package org.brahms5.calendar.server.db.calendar;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.brahms5.calendar.domain.Calendar;
import org.springframework.jdbc.core.RowMapper;

public class CalendarRowMapper implements RowMapper<Calendar> {

	@Override
	public Calendar mapRow(ResultSet rs, int rowNum) throws SQLException {
		CalendarResultSetExtractor extractor = new CalendarResultSetExtractor();
		return extractor.extractData(rs);
	}

}
