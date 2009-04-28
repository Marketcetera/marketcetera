package org.marketcetera.photon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utility class that represents a particular time of day (hour, minute,
 * second).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TimeOfDay {

	/**
	 * Create a new object from provided parameters.
	 * 
	 * @param hour
	 *            the hour of the day
	 * @param minute
	 *            the minute of the hour
	 * @param second
	 *            the second of the minute
	 * @param timeZone
	 *            the time zone assumed by the other parameters
	 * @return the new TimeOfDay object
	 */
	public static TimeOfDay create(int hour, int minute, int second,
			TimeZone timeZone) {
		Calendar in = getCalendar(hour, minute, second, timeZone);
		Calendar out = Calendar.getInstance(UTC);
		out.setTime(in.getTime());
		return create(out);
	}

	/**
	 * Create a new object from a string. The string must be formatted according
	 * to <code>h:mm:ss a z</code> as described in {@link SimpleDateFormat}. If
	 * the format is invalid, <code>null</code> will be returned.
	 * 
	 * @param string
	 *            the string to parse
	 * @return the new TimeOfDay object, or null if the string could not be
	 *         parsed
	 */
	public static TimeOfDay create(String string) {
		try {
			Date date = sFormat.parse(string);
			Calendar calendar = Calendar.getInstance(UTC);
			calendar.setTime(date);
			return create(calendar);
		} catch (ParseException e) {
			ExceptUtils.swallow(e);
			return null;
		}
	}

	private static final int ONE_DAY = 86400000; // 24*60*60*1000

	private static final TimeZone UTC = TimeZone.getTimeZone("UTC"); //$NON-NLS-1$

	private static final DateFormat sFormat;

	static {
		sFormat = new SimpleDateFormat("h:mm:ss a z"); //$NON-NLS-1$
		sFormat.setTimeZone(UTC);
	}

	private static TimeOfDay create(Calendar calendar) {
		return new TimeOfDay(calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
	}

	private static Calendar getCalendar(int hour, int minute, int second,
			TimeZone timeZone) {
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar;
	}

	private int mHour;
	private int mMinute;
	private int mSecond;

	private TimeOfDay(int hour, int minute, int second) {
		mHour = hour;
		mMinute = minute;
		mSecond = second;
	}
	
	/**
	 * Returns the hour in the provided time zone. This returns the hour from 0 to 23 (military
	 * time).
	 * 
	 * @return the hour in the provided time zone
	 */
	public int getHour(TimeZone timeZone) {
		return getCalendar(timeZone).get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Returns the minute in the provided time zone.
	 * 
	 * @return the minute in the provided time zone
	 */
	public int getMinute(TimeZone timeZone) {
		return getCalendar(timeZone).get(Calendar.MINUTE);
	}

	/**
	 * Returns the second in the provided time zone.
	 * 
	 * @return the second in the provided time zone
	 */
	public int getSecond(TimeZone timeZone) {
		return getCalendar(timeZone).get(Calendar.SECOND);
	}

	/**
	 * This object formatted as a string according to <code>h:mm:ss a z</code>
	 * as described by {@link SimpleDateFormat}.
	 * 
	 * It is guaranteed that this object can be recreated from the returned
	 * string using {@link #create(String)}.
	 * 
	 * @return the string representation of this object
	 */
	public String toFormattedString() {
		Calendar calendar = getCalendar();
		return sFormat.format(calendar.getTime());
	}

	/**
	 * Returns the last occurrence of this time of day, that is, the latest
	 * {@link Date} that has the same time of day as this object and is also
	 * earlier than the current time as determined by the system time.
	 * 
	 * It is guaranteed that the {@link Date} object returned will be less than
	 * or equal to any system time computed after this method returns.
	 * 
	 * @return the last occurrence
	 */
	public Date getLastOccurrence() {
		Calendar now = Calendar.getInstance(UTC);
		Calendar calendar = getCalendar();
		calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
		calendar.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR));
		if (calendar.after(now)) {
			return new Date(calendar.getTime().getTime() - ONE_DAY);
		}
		return calendar.getTime();
	}

	private Calendar getCalendar() {
		return getCalendar(mHour, mMinute, mSecond, UTC);
	}

	private Calendar getCalendar(TimeZone timeZone) {
		Calendar utc = getCalendar();
		Calendar out = Calendar.getInstance(timeZone);
		out.setTime(utc.getTime());
		return out;
	}

}
