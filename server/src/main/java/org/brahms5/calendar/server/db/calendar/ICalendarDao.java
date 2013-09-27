package org.brahms5.calendar.server.db.calendar;
import java.util.List;

import javax.sql.DataSource;

import org.brahms5.calendar.domain.Calendar;
import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.server.db.IDao;

public interface ICalendarDao extends IDao{

	void setDataSource(DataSource ds);

	Calendar create(User user);

	void update(Calendar calendar);

	Calendar select(User user);

	List<Calendar> selectAll();
	void insert(Calendar calendar);
	Integer count();
}
