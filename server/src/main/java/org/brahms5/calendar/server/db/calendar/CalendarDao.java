package org.brahms5.calendar.server.db.calendar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.SerializationUtils;
import org.brahms5.calendar.domain.Calendar;
import org.brahms5.calendar.domain.User;
import org.brahms5.calendar.server.db.ADao;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class CalendarDao extends ADao implements ICalendarDao {
	public static final String COLUMNS_CALENDAR = " username, blob ";
	public static final String TABLE_NAME = " calendars ";
	public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + "username varchar2(255)," + "blob blob);";
	public static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + "("
			+ COLUMNS_CALENDAR + ")" + " VALUES (?, ?);";
	public static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
			+ " SET blob= ? WHERE username= ?;";
	public static final String SQL_SELECT = "SELECT " + COLUMNS_CALENDAR
			+ " FROM " + TABLE_NAME + " WHERE username = ?;";

	public static final String SQL_SELECT_ALL = "SELECT " + COLUMNS_CALENDAR
			+ " FROM " + TABLE_NAME + ";";
	public static final String DELETE = "DELETE FROM " + TABLE_NAME
			+ " WHERE username=?";

	@Override
	public void insert(Calendar calendar) {
		log.trace("insert: " + calendar);
		JdbcTemplate insert = new JdbcTemplate(dataSource);
		insert.update(SQL_INSERT, new Object[] { calendar.getUser().getName(),
				SerializationUtils.serialize(calendar) });
	}

	@Override
	public Calendar create(User user) {
		log.trace("create: " + user);

		Calendar calendar = select(user);

		if (calendar == null) {
			log.warn("Calendar already created");
		} else {
			calendar = new Calendar();
			calendar.setUser(user);
			JdbcTemplate insert = new JdbcTemplate(dataSource);
			insert.update(SQL_INSERT, new Object[] { user.getName(),
					SerializationUtils.serialize(calendar) });
		}
		return calendar;
	}

	@Override
	public void update(Calendar calendar) {
		log.trace("update: " + calendar);
		JdbcTemplate update = new JdbcTemplate(dataSource);
		update.update(SQL_UPDATE,
				new Object[] { SerializationUtils.serialize(calendar),
						calendar.getUser().getName() });

	}

	@Override
	public Calendar select(User user) {
		log.trace("select: " + user);
		JdbcTemplate select = new JdbcTemplate(dataSource);
		List<Calendar> list = select.query(SQL_SELECT,
				new Object[] { user.getName() }, new CalendarRowMapper());
		if (list.isEmpty()) {
			log.trace("Returning null");
			return null;
		} else {
			Calendar calender = list.get(0);
			log.trace("Returning: " + calender);
			return calender;
		}
	}

	@Override
	public List<Calendar> selectAll() {
		log.trace("selectAll()");

		JdbcTemplate select = new JdbcTemplate(dataSource);
		List<Calendar> list = select.query(SQL_SELECT_ALL,
				new CalendarRowMapper());
		log.trace("selectAll() returning " + list.size() + " calendars");
		return list;
	}

	@Override
	public void setDataSource(DataSource ds) {
		super.setDataSource(ds);
		JdbcTemplate create = new JdbcTemplate(ds);
		try {
			log.trace("Creating table");
			create.execute(SQL_CREATE);
			log.trace("Creating table success");
		} catch (DataAccessException ex) {
			log.warn("Can't create table", ex);
		}

	}

	@Override
	public Integer count() {
		JdbcTemplate count = new JdbcTemplate(dataSource);
		return count.query("SELECT count(*) FROM " + TABLE_NAME,
				new RowMapper<Integer>() {

					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}

				}).get(0);
	}

	@Override
	public void delete(String username)
	{
		log.trace("deleting: " + username);
		JdbcTemplate delete = new JdbcTemplate(dataSource);
		int rows = delete.update(DELETE, new Object[]{username});
		log.trace("total rows deleted: " + rows);
	}
}
