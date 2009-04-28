package org.marketcetera.photon.views;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class OptionDateHelper
    implements Messages
{
	public static final DecimalFormat MATURITY_MONTH_NUMBER_FORMAT = new DecimalFormat("00"); //$NON-NLS-1$
	public static final DecimalFormat MATURITY_YEAR_NUMBER_FORMAT = new DecimalFormat("0000"); //$NON-NLS-1$

	public List<String> createDefaultMonths() {
		return Arrays.asList(SHORT_MONTH_STRINGS);
	}
	
	public List<String> createDefaultYears() {
		List<String> yearList = new ArrayList<String>();
		final int maxYear = 12;
		for (int currentYear = 8; currentYear <= maxYear; ++currentYear) {
			StringBuilder year = new StringBuilder();
			if (currentYear < 10) {
				year.append("0"); //$NON-NLS-1$
			}
			year.append(currentYear);
			yearList.add(year.toString());
		}
		return yearList;
	}


	public static final DateFormat SHORT_MONTH_FORMAT = new SimpleDateFormat("MMM"); //$NON-NLS-1$
	public static final String [] SHORT_MONTH_STRINGS;

	static {
		Calendar calendar = GregorianCalendar.getInstance();
		SHORT_MONTH_STRINGS = new String[12];
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i < 12; i++) {
			calendar.set(Calendar.MONTH, i);
			SHORT_MONTH_STRINGS[i] = SHORT_MONTH_FORMAT.format(calendar.getTime()).toUpperCase();
		}
	}

	public int parseMonthNumber(String monthAsNumber) throws NumberFormatException {
		int monthNumber = Integer.parseInt(monthAsNumber);
		return monthNumber;
	}
	
	public int getMonthNumber(String shortMonthName){
		for (int i = 0; i < 12; i++){
			if (SHORT_MONTH_STRINGS[i].equals(shortMonthName)){
				return i+1;
			}
		}
		throw new IllegalArgumentException(INVALID_MONTH_NAME.getText(shortMonthName));
	}


	
	/**
	 * @return monthAsNumber "02" returns FEB
	 */
	public String getMonthAbbreviation(String monthAsNumber) {
		int monthNumber = parseMonthNumber(monthAsNumber);
		return getMonthAbbreviation(monthNumber);
	}
	
	/**
	 * @param monthNumber 1 for JAN, 7 for JUL, this is the usual value of Calendar.MONTH + 1   
	 */
	public String getMonthAbbreviation(int monthNumber)
			throws NumberFormatException {
		return SHORT_MONTH_STRINGS[monthNumber-1];
	}
	
	public int calculateYearFromMonth(int expirationMonth) {
		Calendar calendar = GregorianCalendar.getInstance();
		int thisYear = calendar.get(Calendar.YEAR);
		if (calendar.get(Calendar.MONTH) > expirationMonth){
			return thisYear+1;
		} else {
			return thisYear;
		}
	}

	/**
	 * @param expirationMonthNumber the month in human readable numbers (that is 1=JAN)
	 * @param expirationYear they year
	 * @return the formatted string
	 */
	public String formatMaturityMonthYear(int expirationMonthNumber,
			int expirationYear) {
		return formatYear(expirationYear)+
			MATURITY_MONTH_NUMBER_FORMAT.format(expirationMonthNumber);
	}

	/**
	 * @param expirationDayNumber the day
	 * @param expirationMonthNumber the month in human readable numbers (that is 1=JAN)
	 * @param expirationYear they year
	 * @return the formatted string
	 */
	public String formatMaturityDate(int expirationDayNumber, int expirationMonthNumber,
			int expirationYear) {
		return formatYear(expirationYear)+
			MATURITY_MONTH_NUMBER_FORMAT.format(expirationMonthNumber) +
			MATURITY_MONTH_NUMBER_FORMAT.format(expirationDayNumber);
	}

	public String formatYear(int expirationYear) {
		return MATURITY_YEAR_NUMBER_FORMAT.format(expirationYear);
	}


}
