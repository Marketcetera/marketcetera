package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.INVALID_DATE;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;

/**
 * Offers date translation utilities for {@link MarketDataRequest} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequest.java 10390 2009-03-07 04:53:17Z colin $
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DateUtils
{
    private static final DateTimeFormatter YEAR = new DateTimeFormatterBuilder().appendYear(4,
                                                                                                   4).toFormatter();
    private static final DateTimeFormatter MONTH = new DateTimeFormatterBuilder().appendMonthOfYear(2).toFormatter();
    private static final DateTimeFormatter DAY = new DateTimeFormatterBuilder().appendDayOfMonth(2).toFormatter();
    private static final DateTimeFormatter T = new DateTimeFormatterBuilder().appendLiteral('T').toFormatter();
    private static final DateTimeFormatter HOUR = new DateTimeFormatterBuilder().appendHourOfDay(2).toFormatter();
    private static final DateTimeFormatter MINUTE = new DateTimeFormatterBuilder().appendMinuteOfHour(2).toFormatter();
    private static final DateTimeFormatter SECOND = new DateTimeFormatterBuilder().appendSecondOfMinute(2).toFormatter();
    private static final DateTimeFormatter MILLI = new DateTimeFormatterBuilder().appendMillisOfSecond(3).toFormatter();
    private static final DateTimeFormatter TZ = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", //$NON-NLS-1$
                                                                                                    true,
                                                                                                    2,
                                                                                                    4).toFormatter();
    /**
     * valid date formats
     */
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
      // yyyyMMdd'T'HHmmssSSSZ
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLI).append(TZ).toFormatter().withZone(DateTimeZone.UTC),
      // yyyyMMdd'T'HHmmssSSS
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLI).toFormatter().withZone(DateTimeZone.UTC),
      // yyyyMMdd'T'HHmmssZ
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(TZ).toFormatter().withZone(DateTimeZone.UTC),
      // yyyyMMdd'T'HHmmss
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).toFormatter().withZone(DateTimeZone.UTC),
      // yyyyMMdd'T'HHmmZ
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(TZ).toFormatter().withZone(DateTimeZone.UTC),
      // yyyyMMdd'T'HHmm
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).toFormatter().withZone(DateTimeZone.UTC),
      // yyyyMMddZ
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(TZ).toFormatter().withZone(DateTimeZone.UTC),
      // yyyyMMdd
      new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).toFormatter().withZone(DateTimeZone.UTC) };
    /**
     * default date pattern
     */
    private static final DateTimeFormatter DEFAULT_FORMAT = DATE_FORMATS[0];
    /**
     * Converts the given <code>Date</code> value to a <code>String</code> representation usable with
     * {@link MarketDataRequest} objects.
     * 
     * <p>The format of the returned value is ISO 8601 basic format to millisecond precision with
     * time zone offset.  This format can be expressed as: <code>yyyyMMdd'T'HHmmssSSSZ</code> in terms of the format expected
     * by {@link http://java.sun.com/javase/6//docs/api/java/text/SimpleDateFormat.html}.  This format
     * can be used with {@link MarketDataRequest#newRequestFromString(String)} and
     * {@link MarketDataRequest#asOf(String)}.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>String</code> value
     */
    public static String dateToString(Date inDate)
    {
        return DEFAULT_FORMAT.print(new DateTime(inDate));
    }
    /**
     * Parses the given <code>String</code> to a <code>Date</code> value.
     * 
     * <p>The given <code>String</code> is expected to be formatted in ISO 8601 basic format as described
     * by {@link DateUtils#dateToString(Date)}.  The following formats are accepted:
     * <ul>
     *   <li>yyyyMMdd'T'HHmmssSSSZ (e.g. 20090303T224025444-0800)</li>
     *   <li>yyyyMMdd'T'HHmmssSSS (e.g. 20090303T224025444)</li>
     *   <li>yyyyMMdd'T'HHmmssZ (e.g. 20090303T224025-0800)</li>
     *   <li>yyyyMMdd'T'HHmmss (e.g. 20090303T224025)</li>
     *   <li>yyyyMMdd'T'HHmmZ (e.g. 20090303T2240-0800)</li>
     *   <li>yyyyMMdd'T'HHmm (e.g. 20090303T2240)</li>
     *   <li>yyyyMMddZ (e.g. 20090303-0800)</li>
     *   <li>yyyyMMdd (e.g. 20090303)</li>
     * </ul>
     * 
     * <p>If the timezone offset is omitted from the given <code>String</code>, the date/time is assumed
     * to be in UTC.  If omitted, milliseconds, seconds, minutes, and hours are set to zero.  Fields
     * may not be abbreviated, i.e., minutes must contain two digits even if the value is less than ten,
     * <code>01</code> instead of <code>1</code>.  Timezone offsets may be expressed as <code>Z</code>
     * for UTC or as an offset from UTC indicated by <code>+</code> or <code>-</code> and four digits.
     * With the exception of the timezone offset indicator, no punctuation is allowed in the expression.
     *
     * @param inDateString a <code>String</code> value a <code>String</code> containing a date value to be
     *  parsed.
     * @return a <code>Date</code> value 
     * @throws MarketDataRequestException if the given <code>String</code> could not be parsed 
     */
    public static Date stringToDate(String inDateString)
        throws MarketDataRequestException
    {
        if(inDateString == null ||
           inDateString.isEmpty()) {
            throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_DATE,
                                                                        inDateString));
        }
        for(int formatCounter=0;formatCounter<DATE_FORMATS.length;formatCounter++) {
            try {
                return new Date(DATE_FORMATS[formatCounter].parseDateTime(inDateString).getMillis());
            } catch (IllegalArgumentException e) {
                // this format didn't work, try a less specific one
            }
        }
        throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_DATE,
                                                                    inDateString));
    }
}