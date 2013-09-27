package org.brahms5.calendar.server.db;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADao implements IDao {

	protected Logger log = LoggerFactory.getLogger(this.getClass());
	protected DataSource dataSource = null;
	@Override
	public void setDataSource(DataSource ds) {
		dataSource = ds;
	}
}
