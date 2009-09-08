package org.marketcetera.photon.ui.validation.fix;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.databinding.conversion.Converter;
import org.marketcetera.photon.Messages;

public class StringToDateCustomConverter
    extends Converter
    implements Messages
{
	private String [] dateFormatStrings;

	private boolean forceUppercase;

	private boolean emptyStringBecomesNullDate;

	private SimpleDateFormat [] formatters;

	private String humanReadableFormatStrings;

	/**
	 * Force toUpperCase before parsing. Empty strings convert to null dates.
	 */
	public StringToDateCustomConverter(String ... inDateFormatStrings) {
		this(true, true, inDateFormatStrings);
	}

	/**
	 * @param inDateFormatStrings
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
	public StringToDateCustomConverter(
			boolean forceUppercase, boolean emptyStringBecomesNullDate, String ... inDateFormatStrings) {
		super(String.class, java.util.Date.class);
		this.dateFormatStrings = inDateFormatStrings;
		this.humanReadableFormatStrings = ""; //$NON-NLS-1$
		this.formatters = new SimpleDateFormat[dateFormatStrings.length];
		int numFormatStrings = inDateFormatStrings.length;
		for (int i = 0; i < numFormatStrings; i++) {
			String aFormatString = inDateFormatStrings[i];
			this.formatters[i] = new SimpleDateFormat(aFormatString);
			humanReadableFormatStrings += aFormatString;
			if (i < numFormatStrings - 1){
				humanReadableFormatStrings += ", "; //$NON-NLS-1$
			}
		}
		this.forceUppercase = forceUppercase;
		this.emptyStringBecomesNullDate = emptyStringBecomesNullDate;
	}

	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return null;
		}
		if (!(fromObject instanceof String)) {
			throw new IllegalArgumentException(INVALID_SPECIFIED_VALUE.getText(fromObject));
		}
		String fromDateString = (String) fromObject;
		fromDateString = fromDateString.trim();
		if (emptyStringBecomesNullDate && fromDateString.length() == 0) {
			return null;
		}
		if (forceUppercase) {
			fromDateString = fromDateString.toUpperCase();
		}
		Date toDate = null;
		for (int i = 0; i < formatters.length; i++) {
			SimpleDateFormat formatter = formatters[i];
			try {
				synchronized (formatter) {
					if (fromDateString.length() == dateFormatStrings[i].length()){
						toDate = formatter.parse(fromDateString);
					}
				}
			} catch (java.text.ParseException e) {
			}
		} 
		if (toDate == null){
			throw new IllegalArgumentException(INVALID_SPECIFIED_DATE.getText(fromDateString,
			                                                                  humanReadableFormatStrings));
		}
		return toDate;
	}

}