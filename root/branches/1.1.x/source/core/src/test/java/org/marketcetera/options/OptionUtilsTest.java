package org.marketcetera.options;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class OptionUtilsTest extends TestCase {
	public void testGetUSEquityOptionExpiration() throws Exception {
		assertExpiration(20, OptionUtils.getUSEquityOptionExpiration(Calendar.JANUARY, 2007));
		assertExpiration(17, OptionUtils.getUSEquityOptionExpiration(Calendar.FEBRUARY, 2007));
		assertExpiration(17, OptionUtils.getUSEquityOptionExpiration(Calendar.MARCH, 2007));
		assertExpiration(21, OptionUtils.getUSEquityOptionExpiration(Calendar.APRIL, 2007));
		assertExpiration(19, OptionUtils.getUSEquityOptionExpiration(Calendar.MAY, 2007));
		assertExpiration(16, OptionUtils.getUSEquityOptionExpiration(Calendar.JUNE, 2007));
		assertExpiration(21, OptionUtils.getUSEquityOptionExpiration(Calendar.JULY, 2007));
		assertExpiration(18, OptionUtils.getUSEquityOptionExpiration(Calendar.AUGUST, 2007));
		assertExpiration(22, OptionUtils.getUSEquityOptionExpiration(Calendar.SEPTEMBER, 2007));
		assertExpiration(20, OptionUtils.getUSEquityOptionExpiration(Calendar.OCTOBER, 2007));
		assertExpiration(17, OptionUtils.getUSEquityOptionExpiration(Calendar.NOVEMBER, 2007));
		assertExpiration(22, OptionUtils.getUSEquityOptionExpiration(Calendar.DECEMBER, 2007));
		assertExpiration(19, OptionUtils.getUSEquityOptionExpiration(Calendar.JANUARY, 2008));
		assertExpiration(16, OptionUtils.getUSEquityOptionExpiration(Calendar.FEBRUARY, 2008));
		assertExpiration(22, OptionUtils.getUSEquityOptionExpiration(Calendar.MARCH, 2008));
		assertExpiration(19, OptionUtils.getUSEquityOptionExpiration(Calendar.APRIL, 2008));
		assertExpiration(17, OptionUtils.getUSEquityOptionExpiration(Calendar.MAY, 2008));
		assertExpiration(21, OptionUtils.getUSEquityOptionExpiration(Calendar.JUNE, 2008));
		assertExpiration(19, OptionUtils.getUSEquityOptionExpiration(Calendar.JULY, 2008));
		assertExpiration(16, OptionUtils.getUSEquityOptionExpiration(Calendar.AUGUST, 2008));
		assertExpiration(20, OptionUtils.getUSEquityOptionExpiration(Calendar.SEPTEMBER, 2008));
		assertExpiration(18, OptionUtils.getUSEquityOptionExpiration(Calendar.OCTOBER, 2008));
		assertExpiration(22, OptionUtils.getUSEquityOptionExpiration(Calendar.NOVEMBER, 2008));
		assertExpiration(20, OptionUtils.getUSEquityOptionExpiration(Calendar.DECEMBER, 2008));
	}

	private void assertExpiration(int dayOfMonth, Date equityOptionExpiration) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(equityOptionExpiration);
		assertEquals(dayOfMonth, cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public void testGetNextUSEquityOptionExpiration() throws Exception {
		long currentTimeMillis = System.currentTimeMillis();
		Date date = OptionUtils.getNextUSEquityOptionExpiration();
//		assertTrue(currentTimeMillis < date.getTime());
	}
	
	
}
