import static org.junit.Assert.*;
import groovy.util.logging.Slf4j;

import org.brahms5.calendar.client.AShell
import org.junit.Test;

@Slf4j
class AShellTest extends AShell{

	@Test
	public void testGetTime() {
		assert null != getTime("12/12/12")
		assert null != getTime("12/12/12 23:23")
		assert null != getTime("2013-02-02")
		assert null != getTime("2013-02-02 23:23")
		assert null != getTime("12/12/1986")
		assert null != getTime("12/12/1986 23:23")
		assert null != getTime("23:23")
		assert null != getTime("10 am")
	}

}
