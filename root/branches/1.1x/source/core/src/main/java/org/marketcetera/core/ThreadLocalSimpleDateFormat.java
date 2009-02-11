package org.marketcetera.core;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Because {@link SimpleDateFormat} is not threadsafe, this class provides
 * a simple alternative to explicit synchronization.  This subclass of
 * {@link ThreadLocal} will create an instance of SimpleDateFormat
 * for each thread that accesses the variable.
 * 
 * For convenience parallel constructors are provided for each form
 * of the SimpleDateFormat constructor.
 * 
 * The implementation of {@link #initialValue()} provides the
 * instance of SimpleDateFormat for each thread.
 * 
 * @author gmiller
 *
 */
public class ThreadLocalSimpleDateFormat extends ThreadLocal<SimpleDateFormat> {

	private final String formatString;
	private final Locale locale;
	private TimeZone timeZone;

	/**
	 * Constructor that invokes the String construtor of SimpleDateFormat
	 * on each accessing thread.
	 * 
     * @exception NullPointerException if the given pattern is null
     * @exception IllegalArgumentException if the given pattern is invalid
	 * @see SimpleDateFormat#SimpleDateFormat(String)
	 */
	public ThreadLocalSimpleDateFormat(String formatString) {
		this(formatString, null);
	}
	

	/**
	 * Constructor that invokes the (String, Locale) constructor
	 * on each accessing thread.
	 * 
     * @exception NullPointerException if the given pattern is null
     * @exception IllegalArgumentException if the given pattern is invalid
	 * @see SimpleDateFormat#SimpleDateFormat(String, Locale)
	 */
	public ThreadLocalSimpleDateFormat(String formatString, Locale locale) {
		this.formatString = formatString;
		this.locale = locale;
		new SimpleDateFormat(formatString);
	}

	@Override
	protected SimpleDateFormat initialValue() {  //i18n_datetime
		SimpleDateFormat simpleDateFormat;
		if (locale != null){
			simpleDateFormat = new SimpleDateFormat(formatString, locale);
		} else {
			simpleDateFormat = new SimpleDateFormat(formatString);
		}
		if (timeZone != null){
			simpleDateFormat.setTimeZone(timeZone);
		}
		return simpleDateFormat;
	}

	/**
	 * Sets the timeZone string that will be set on each
	 * instance of SimpleDateFormat created by this object.
	 * 
	 * @param timeZone the time zone as a string
	 * @see SimpleDateFormat#setTimeZone(TimeZone)
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
}
