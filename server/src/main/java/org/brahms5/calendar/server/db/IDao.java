package org.brahms5.calendar.server.db;

import javax.sql.DataSource;

public abstract interface IDao {

	  void setDataSource(DataSource ds);
}
