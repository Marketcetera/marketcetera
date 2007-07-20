package org.marketcetera.photon.views;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.marketcetera.photon.marketdata.OptionMarketDataUtils;

public class OptionDateHelper {
	private SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM");
	public static final DecimalFormat MATURITY_MONTH_NUMBER_FORMAT = new DecimalFormat("00");
	public static final DecimalFormat MATURITY_YEAR_NUMBER_FORMAT = new DecimalFormat("0000");

	public List<String> createDefaultMonths() {
		List<String> monthStrings = new ArrayList<String>();

		GregorianCalendar calendar = new GregorianCalendar();
		final int minMonth = calendar.getMinimum(Calendar.MONTH);
		final int maxMonth = calendar.getMaximum(Calendar.MONTH);
		for (int month = minMonth; month <= maxMonth; ++month) {
			calendar.set(Calendar.MONTH, month);
			java.util.Date monthTime = calendar.getTime();
			String monthStr = monthFormatter.format(monthTime);
			monthStr = monthStr.toUpperCase();
			monthStrings.add(monthStr);
		}
		return monthStrings;
	}
	
	public List<String> createDefaultYears() {
		List<String> yearList = new ArrayList<String>();
		final int maxYear = 12;
		for (int currentYear = 7; currentYear <= maxYear; ++currentYear) {
			StringBuilder year = new StringBuilder();
			if (currentYear < 10) {
				year.append("0");
			}
			year.append(currentYear);
			yearList.add(year.toString());
		}
		return yearList;
	}


	public static final DateFormat SHORT_MONTH_FORMAT = new SimpleDateFormat("MMM");
	public static final String [] SHORT_MONTH_STRINGS;

	static {
		Calendar calendar = GregorianCalendar.getInstance();
		SHORT_MONTH_STRINGS = new String[12];
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
		throw new IllegalArgumentException("monthName must be a valid month name");
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
	 * 
	 * @param expirationMonth the month in human readable numbers (that is 1=JAN)
	 * @param expirationYear
	 * @return
	 */
	public String formatMaturityMonthYear(int expirationMonthNumber,
			int expirationYear) {
		return formatYear(expirationYear)+
			MATURITY_MONTH_NUMBER_FORMAT.format(expirationMonthNumber);
	}

	/**
	 * 
	 * @param expirationMonth the month in human readable numbers (that is 1=JAN)
	 * @param expirationYear
	 * @return
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
