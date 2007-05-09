package org.marketcetera.photon.ui.validation.fix;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.databinding.conversion.Converter;

public class StringToDateCustomConverter extends Converter {
	private SimpleDateFormat formatter;

	private String dateFormatStr;

	private boolean forceUppercase;

	private boolean emptyStringBecomesNullDate;

	/**
	 * Force toUpperCase before parsing. Empty strings convert to null dates.
	 */
	public StringToDateCustomConverter(String dateFormatStr) {
		this(dateFormatStr, true, true);
	}

	/**
	 * @param dateFormatStr
	 *            a date format string of the form specified by
	 *            SimpleDateFormat. *
	 * @param forceUppercase
	 *            when true, force toUpperCase before parsing date values.
	 * @param emptyStringBecomesNullDate
	 *            when true, convert any empty strings to a null date. When
	 *            false, empty string will cause an IllegalArgumentException on
	 *            convert.
	 * @see org.marketcetera.photon.ui.validation.fix.DateToStringCustomConverter
	 *      for predefined date formats.
	 */
	public StringToDateCustomConverter(String dateFormatStr,
			boolean forceUppercase, boolean emptyStringBecomesNullDate) {
		super(String.class, java.util.Date.class);
		this.dateFormatStr = dateFormatStr;
		this.formatter = new SimpleDateFormat(dateFormatStr);
		this.forceUppercase = forceUppercase;
		this.emptyStringBecomesNullDate = emptyStringBecomesNullDate;
	}

	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return null;
		}
		if (!(fromObject instanceof String)) {
			throw new IllegalArgumentException("The value: " + fromObject
					+ " is not valid.");
		}
		String fromDateString = (String) fromObject;
		fromDateString = fromDateString.trim();
		if (emptyStringBecomesNullDate && fromDateString.length() == 0) {
			return null;
		}
		if (fromDateString.length() <= 0
				|| fromDateString.length() > dateFormatStr.length()) {
			throw new IllegalArgumentException("The value: " + fromDateString
					+ " is not a valid date of the form: " + dateFormatStr);
		}
		if (forceUppercase) {
			fromDateString = fromDateString.toUpperCase();
		}
		Date toDate = null;
		try {
			synchronized (formatter) {
				toDate = formatter.parse(fromDateString);
			}
		} catch (java.text.ParseException e) {
			throw new IllegalArgumentException("The value: " + fromDateString
					+ " is not a valid date of the form: " + dateFormatStr);
		}
		return toDate;
	}

}