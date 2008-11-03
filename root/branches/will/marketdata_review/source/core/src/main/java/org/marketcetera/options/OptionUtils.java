package org.marketcetera.options;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OptionUtils {

	/**
	 * 
	 * @param month one of Calendar.JANUARY - Calendar.DECEMBER
	 */
	public static final Date getUSEquityOptionExpiration(int month, int year){
		Calendar cal = GregorianCalendar.getInstance(); //i18n_date
		cal.set(year, month, 1);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int firstFridayOffset = (Calendar.FRIDAY - dayOfWeek + 7)%7;

		cal.add(Calendar.DAY_OF_MONTH, firstFridayOffset + 15); // two weeks and a day
		return cal.getTime();
	}
	
	public static final Date getNextUSEquityOptionExpiration(){
		Calendar cal = GregorianCalendar.getInstance();    //i18n_datetime
		long currentTime = cal.getTimeInMillis();
		int currentMonth = cal.get(Calendar.MONTH);
		int currentYear = cal.get(Calendar.YEAR);
		Date candidate = getUSEquityOptionExpiration(currentMonth, currentYear);
		if (currentTime > candidate.getTime()){
			candidate = getUSEquityOptionExpiration(currentMonth+1, currentYear);
		} 
		return candidate;
	}
}
