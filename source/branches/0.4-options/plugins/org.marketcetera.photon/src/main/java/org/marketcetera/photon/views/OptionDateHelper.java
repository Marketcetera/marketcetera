package org.marketcetera.photon.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class OptionDateHelper {
	private SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM");

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

	public int getMonthNumber(String monthAsNumber) throws NumberFormatException {
		int monthNumber = Integer.parseInt(monthAsNumber);
		return monthNumber;
	}

	/**
	 * @return monthAsNumber "02" returns FEB
	 */
	public String getMonthAbbreviation(String monthAsNumber) {
		int monthNumber = getMonthNumber(monthAsNumber);
		return getMonthAbbreviation(monthNumber);
	}
	
	public String getMonthAbbreviation(int monthNumber)
			throws NumberFormatException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.MONTH, monthNumber);
		java.util.Date monthTime = calendar.getTime();
		String monthStr = monthFormatter.format(monthTime);
		monthStr = monthStr.toUpperCase();
		return monthStr;
	}
}
