package org.marketcetera.photon.ui.validation.fix;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class DateToStringCustomConverterTest extends TestCase {

	private void performConvertToString( String dateFormat, int calendarField, int calendarValue, String expectedDateStr ) {
		DateToStringCustomConverter converter = new DateToStringCustomConverter( dateFormat );
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.set(calendarField, calendarValue);
		Date expectedDate = calendar.getTime();
		String actualDateStr = (String) converter.convert(expectedDate);
		assertEquals(expectedDateStr, actualDateStr);
	}
	
	public void testDateToStringConverter_MonthFormat() {
		String format = DateToStringCustomConverter.MONTH_FORMAT;
		int calendarField = Calendar.MONTH;
		performConvertToString(format, calendarField, 0, "JAN");
		performConvertToString(format, calendarField, 11, "DEC");
		performConvertToString(format, calendarField, 6, "JUL");
	}
	
	public void testDateToStringConverter_YearFormat() {
		String format = DateToStringCustomConverter.SHORT_YEAR_FORMAT;
		int calendarField = Calendar.YEAR;
		performConvertToString(format, calendarField, 2007, "07");
		performConvertToString(format, calendarField, 2008, "08");
		performConvertToString(format, calendarField, 2012, "12");
	}
	
	
	private void performConvertToDate( String dateFormat, int calendarField, String inputDateStr, int expectedCalendarFieldValue ) {
		StringToDateCustomConverter converter = new StringToDateCustomConverter( dateFormat );
		
		Date actualDate = (Date) converter.convert(inputDateStr);
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(actualDate);
		int actualFieldValue = calendar.get(calendarField);
		assertEquals( expectedCalendarFieldValue, actualFieldValue );
	}
	
	public void testStringToDateConverter_MonthFormat() {
		String format = DateToStringCustomConverter.MONTH_FORMAT;
		int calendarField = Calendar.MONTH;
		performConvertToDate(format, calendarField, "JAN", 0);
		performConvertToDate(format, calendarField, "DEC", 11);
		performConvertToDate(format, calendarField, "JUL", 6);
	}
	
	public void testStringToDateConverter_YearFormat() {
		String format = DateToStringCustomConverter.SHORT_YEAR_FORMAT;
		int calendarField = Calendar.YEAR;
		performConvertToDate(format, calendarField, "07", 2007);
		performConvertToDate(format, calendarField, "08", 2008);
		performConvertToDate(format, calendarField, "12", 2012);
	}
	
}
