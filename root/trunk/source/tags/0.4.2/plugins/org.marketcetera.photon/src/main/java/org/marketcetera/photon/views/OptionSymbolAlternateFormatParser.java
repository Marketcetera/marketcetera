package org.marketcetera.photon.views;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.marketcetera.photon.marketdata.OptionContractData;

import quickfix.field.PutOrCall;

/**
 * Convert option symbols in an alternate format "MSQ October-07 75 Calls" to
 * the standard UI values MSQ, OCT, 2007, 75, C
 * 
 * @author michael.lossos@softwaregoodness.com
 * 
 */
public class OptionSymbolAlternateFormatParser {
	private static final int MINIMUM_YEAR = 2000;
	private static final String PUT_PREFIX = "put";
	private static final String CALL_PREFIX = "call";
	private static final int MINIMUM_SYMBOL_PIECES = 4;

	private final SimpleDateFormat longMonthFormatter = new SimpleDateFormat(
			"MMMMMMMMMM");

	private final Pattern altFormatDelimeter = Pattern.compile("[ \t]+");

	private final Pattern altFormatMonthYearDelimeter = Pattern.compile("-");

	private OptionDateHelper optionDateHelper = new OptionDateHelper();

	public boolean isOptionContractSymbolInAlternateFormat(
			String optionContractSymbol) {
		if (optionContractSymbol == null) {
			return false;
		}
		String[] pieces = altFormatDelimeter.split(optionContractSymbol);
		if (pieces == null || pieces.length < MINIMUM_SYMBOL_PIECES) {
			return false;
		}
		return true;
	}

	/**
	 * @param optionContractSymbolAltFormat
	 *            e.g. "MSQ October-07 75 Calls"
	 */
	public OptionContractData parseOptionContractDataAlternateFormat(
			String optionContractSymbolAltFormat) {
		String[] pieces = altFormatDelimeter
				.split(optionContractSymbolAltFormat);
		if (pieces == null || pieces.length < MINIMUM_SYMBOL_PIECES) {
			return null;
		}
		String[] monthYearPieces = altFormatMonthYearDelimeter.split(pieces[1]);
		if (monthYearPieces == null || monthYearPieces.length < 2) {
			return null;
		}
		String optionRoot = pieces[0];
		String uiExpirationMonth = getMonth(monthYearPieces[0]);
		String uiExpirationYear = null;
		try {
			int yearInt = Integer.parseInt(monthYearPieces[1]);
			if (yearInt < MINIMUM_YEAR) {
				yearInt += MINIMUM_YEAR;
			}
			uiExpirationYear = optionDateHelper.formatYear(yearInt);
		} catch (Exception anyException) {
			// Ignore
		}
		// todo: Handle the situation where the user enters "75" but the actual
		// UI value is "75.0"
		String uiStrikePrice = pieces[2];
		Integer putOrCall = getPutOrCall(pieces[3]);

		if (optionRoot == null || uiExpirationYear == null
				|| uiExpirationMonth == null || uiStrikePrice == null
				|| putOrCall == null) {
			return null;
		}

		OptionContractData contractData = new OptionContractData(optionRoot,
				uiExpirationYear, uiExpirationMonth, uiStrikePrice, putOrCall);
		return contractData;
	}

	/**
	 * @param longMonth
	 *            e.g. October
	 * @return the month value as used in the order ticket UI combo boxes, or
	 *         null on parsing error
	 */
	private String getMonth(String longMonth) {
		int monthInt = getMonthInt(longMonth);
		if (monthInt < 0) {
			return null;
		}
		++monthInt; // parser expects JAN = 1
		return optionDateHelper.getMonthAbbreviation(monthInt);
	}

	/**
	 * @param longMonth
	 *            e.g. October
	 * @return -1 on error, otherwise the integer month, e.g. JAN = 0
	 */
	private int getMonthInt(String longMonth) {
		int monthInt = -1;
		try {
			Date month = longMonthFormatter.parse(longMonth);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(month);
			monthInt = calendar.get(Calendar.MONTH);
		} catch (Exception anyException) {
			// Ignore
		}
		return monthInt;
	}

	/**
	 * Comparison is case insensitive.
	 * 
	 * @param alternatePutOrCall
	 *            "Puts", "Put", "Calls", "Call"
	 * @return null on parsing error
	 */
	private Integer getPutOrCall(String alternatePutOrCall) {
		if (alternatePutOrCall == null) {
			return null;
		}
		String lowercaseAltPutOrCall = alternatePutOrCall.toLowerCase();
		if (lowercaseAltPutOrCall.startsWith(PUT_PREFIX)) {
			return PutOrCall.PUT;
		}
		if (lowercaseAltPutOrCall.startsWith(CALL_PREFIX)) {
			return PutOrCall.CALL;
		}
		return null;
	}
}
