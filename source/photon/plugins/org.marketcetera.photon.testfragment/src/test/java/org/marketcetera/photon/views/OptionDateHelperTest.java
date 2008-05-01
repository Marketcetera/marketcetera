package org.marketcetera.photon.views;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.marketcetera.core.ExpectedTestFailure;

public class OptionDateHelperTest extends TestCase {

	static OptionDateHelper optionDateHelper = new OptionDateHelper();
	
	public void testCreateDefaultMonths() throws Exception {
		assertEquals(12, optionDateHelper.createDefaultMonths().size());
	}
	
	public void testParseMonthNumber() {
		assertEquals(2, optionDateHelper.parseMonthNumber("02"));
		new ExpectedTestFailure(NumberFormatException.class){
			@Override
			protected void execute() throws Throwable {
				optionDateHelper.parseMonthNumber("tferguson");
			}
		}.run();
	}

	public void testGetMonthNumber() {
		assertEquals(1, optionDateHelper.getMonthNumber("JAN"));
		assertEquals(2, optionDateHelper.getMonthNumber("FEB"));
		assertEquals(3, optionDateHelper.getMonthNumber("MAR"));
		assertEquals(4, optionDateHelper.getMonthNumber("APR"));
		assertEquals(5, optionDateHelper.getMonthNumber("MAY"));
		assertEquals(6, optionDateHelper.getMonthNumber("JUN"));
		assertEquals(7, optionDateHelper.getMonthNumber("JUL"));
		assertEquals(8, optionDateHelper.getMonthNumber("AUG"));
		assertEquals(9, optionDateHelper.getMonthNumber("SEP"));
		assertEquals(10, optionDateHelper.getMonthNumber("OCT"));
		assertEquals(11, optionDateHelper.getMonthNumber("NOV"));
		assertEquals(12, optionDateHelper.getMonthNumber("DEC"));
	}

	public void testGetMonthAbbreviationString() {
		assertEquals("JAN", optionDateHelper.getMonthAbbreviation("01"));
		assertEquals("FEB", optionDateHelper.getMonthAbbreviation("02"));
		assertEquals("MAR", optionDateHelper.getMonthAbbreviation("03"));
		assertEquals("APR", optionDateHelper.getMonthAbbreviation("04"));
		assertEquals("MAY", optionDateHelper.getMonthAbbreviation("05"));
		assertEquals("JUN", optionDateHelper.getMonthAbbreviation("06"));
		assertEquals("JUL", optionDateHelper.getMonthAbbreviation("07"));
		assertEquals("AUG", optionDateHelper.getMonthAbbreviation("08"));
		assertEquals("SEP", optionDateHelper.getMonthAbbreviation("09"));
		assertEquals("OCT", optionDateHelper.getMonthAbbreviation("10"));
		assertEquals("NOV", optionDateHelper.getMonthAbbreviation("11"));
		assertEquals("DEC", optionDateHelper.getMonthAbbreviation("12"));
	}

	public void testGetMonthAbbreviationInt() {
		assertEquals("JAN", optionDateHelper.getMonthAbbreviation(1));
		assertEquals("FEB", optionDateHelper.getMonthAbbreviation(2));
		assertEquals("MAR", optionDateHelper.getMonthAbbreviation(3));
		assertEquals("APR", optionDateHelper.getMonthAbbreviation(4));
		assertEquals("MAY", optionDateHelper.getMonthAbbreviation(5));
		assertEquals("JUN", optionDateHelper.getMonthAbbreviation(6));
		assertEquals("JUL", optionDateHelper.getMonthAbbreviation(7));
		assertEquals("AUG", optionDateHelper.getMonthAbbreviation(8));
		assertEquals("SEP", optionDateHelper.getMonthAbbreviation(9));
		assertEquals("OCT", optionDateHelper.getMonthAbbreviation(10));
		assertEquals("NOV", optionDateHelper.getMonthAbbreviation(11));
		assertEquals("DEC", optionDateHelper.getMonthAbbreviation(12));
	}

	public void testFormatYear() {
		// death of Charlemagne
		assertEquals("0814", optionDateHelper.formatYear(814));
		// my birth year
		assertEquals("1974", optionDateHelper.formatYear(1974));
		
	}
	
    public void testCalculateYearFromMonth() throws Exception {
    	Calendar calendar = GregorianCalendar.getInstance();
    	int thisYear = calendar.get(Calendar.YEAR);
    	int nextYear = thisYear +1;
    	int thisMonth = calendar.get(Calendar.MONTH);

    	assertEquals(thisMonth > 0 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(0));
    	assertEquals(thisMonth > 1 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(1));
    	assertEquals(thisMonth > 2 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(2));
    	assertEquals(thisMonth > 3 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(3));
    	assertEquals(thisMonth > 4 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(4));
    	assertEquals(thisMonth > 5 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(5));
    	assertEquals(thisMonth > 6 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(6));
    	assertEquals(thisMonth > 7 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(7));
    	assertEquals(thisMonth > 8 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(8));
    	assertEquals(thisMonth > 9 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(9));
    	assertEquals(thisMonth > 10 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(10));
    	assertEquals(thisMonth > 11 ? nextYear: thisYear, optionDateHelper.calculateYearFromMonth(11));
	}

    public void testFormatMaturityMonthYear() throws Exception {
    	assertEquals("200708", optionDateHelper.formatMaturityMonthYear(8, 2007));
    	assertEquals("200711", optionDateHelper.formatMaturityMonthYear(11, 2007));
	}


}
