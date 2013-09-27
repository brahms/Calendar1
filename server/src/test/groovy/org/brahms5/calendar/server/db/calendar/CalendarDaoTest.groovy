package org.brahms5.calendar.server.db.calendar

import org.apache.commons.lang3.SerializationUtils
import org.brahms5.calendar.domain.Calendar
import org.brahms5.calendar.domain.Event
import org.brahms5.calendar.domain.User
import org.brahms5.calendar.domain.Event.AccessControlMode
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.DatabaseDataSourceConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.Column
import org.dbunit.dataset.DefaultDataSet
import org.dbunit.dataset.DefaultTable
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.datatype.DataType
import org.dbunit.operation.DatabaseOperation
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.jdbc.datasource.DriverManagerDataSource


public class CalendarDaoTest {
	CalendarDao mCalendarDao = null;
	Calendar mCalendar1 = createCalendar1()
	Calendar mCalendar2 = createCalendar2()
	Calendar mCalendar3 = createCalendar3()
	@Before public void setUp() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:test");
		dataSource.setUsername("");
		dataSource.setPassword("");
		IDatabaseConnection connection = new DatabaseDataSourceConnection(dataSource);
		mCalendarDao = new CalendarDao()
		mCalendarDao.setDataSource(dataSource)

		def cols = [new Column("username", DataType.VARCHAR), new Column("blob", DataType.BLOB)] as Column[]
		DefaultTable calendarTable = new DefaultTable("calendars", cols)
		calendarTable.addRow ([
			mCalendar1.getUser().getName(),
			SerializationUtils.serialize(mCalendar1)
		] as Object[])
		calendarTable.addRow ([
			mCalendar2.getUser().getName(),
			SerializationUtils.serialize(mCalendar2)
		] as Object[])
		IDataSet dataSet = new DefaultDataSet(calendarTable);

		try
		{
			DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
		}
        finally
        {
            connection.close();
        }
	}
	@After public void tearDown() {

	}

	@Test public void test()
	{
		assert 1==1
	}

	protected Calendar createCalendar1()
	{
		def cal = new Calendar()
		def user = new User()
		user.setName("cbrahms")
		cal.setUser(user)
		return cal
	}
	protected Calendar createCalendar2()
	{
		def cal = new Calendar()
		def user = new User()
		user.setName("cbrahms2")
		cal.setUser(user)
		def e1 = new Event()
		e1.setAccessControlMode(AccessControlMode.OPEN)
		e1.setDescription("Event 1")

		return cal

	}
	protected Calendar createCalendar3()
	{
		def cal = new Calendar()
		def user = new User()
		user.setName("cbrahms3")
		cal.setUser(user)

		return cal
	}
}
