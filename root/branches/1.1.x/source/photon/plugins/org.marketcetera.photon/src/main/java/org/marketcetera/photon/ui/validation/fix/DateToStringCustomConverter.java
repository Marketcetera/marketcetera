package org.marketcetera.photon.ui.validation.fix;

import java.util.Date;

import org.eclipse.core.databinding.conversion.Converter;
import org.marketcetera.core.ThreadLocalSimpleDateFormat;
import org.marketcetera.photon.Messages;

public class DateToStringCustomConverter
    extends Converter
    implements Messages
{
	public static final String MONTH_FORMAT = "MMM"; //$NON-NLS-1$

	public static final String SHORT_YEAR_FORMAT = "yy"; //$NON-NLS-1$

	public static final String LONG_YEAR_FORMAT = "yyyy"; //$NON-NLS-1$

	private ThreadLocalSimpleDateFormat localFormat;

	private boolean forceUppercase;

	/**
	 * Force uppercase for converted values. 
	 */
	public DateToStringCustomConverter(String dateFormatStr) {
		this(dateFormatStr, true);
	}
	
	/**
	 * @param dateFormatStr
	 *            a date format string of the form specified by
	 *            SimpleDateFormat. Prefer using the predefined date formats in
	 *            this class.
	 * @param forceUppercase
	 *            when true, all values will be returned as toUpperCase
	 */
	public DateToStringCustomConverter(String dateFormatStr, boolean forceUppercase) {
		super(java.util.Date.class, String.class);
		this.localFormat = new ThreadLocalSimpleDateFormat(dateFormatStr);
		this.forceUppercase = forceUppercase;
	}

	public Object convert(Object fromObject) {
		if (fromObject == null) {
			return null;
		}
		if (!(fromObject instanceof Date)) {
			throw new IllegalArgumentException(INVALID_SPECIFIED_DATE.getText(fromObject,
			                                                                  localFormat));
		}
		Date fromDate = (Date) fromObject;
		String toDateString = null;
		toDateString = localFormat.get().format(fromDate);
		if (forceUppercase) {
			toDateString = toDateString.toUpperCase();
		}
		return toDateString;
	}

}
