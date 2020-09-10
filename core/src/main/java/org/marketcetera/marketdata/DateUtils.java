package org.marketcetera.marketdata;

import static org.marketcetera.core.time.TimeFactoryImpl.COLON;
import static org.marketcetera.core.time.TimeFactoryImpl.DASH;
import static org.marketcetera.core.time.TimeFactoryImpl.DAY;
import static org.marketcetera.core.time.TimeFactoryImpl.FULL_MILLISECONDS;
import static org.marketcetera.core.time.TimeFactoryImpl.HOUR;
import static org.marketcetera.core.time.TimeFactoryImpl.MILLISECOND;
import static org.marketcetera.core.time.TimeFactoryImpl.MINUTE;
import static org.marketcetera.core.time.TimeFactoryImpl.MONTH;
import static org.marketcetera.core.time.TimeFactoryImpl.PERIOD;
import static org.marketcetera.core.time.TimeFactoryImpl.SECOND;
import static org.marketcetera.core.time.TimeFactoryImpl.T;
import static org.marketcetera.core.time.TimeFactoryImpl.YEAR;
import static org.marketcetera.core.time.TimeFactoryImpl.Z;
import static org.marketcetera.marketdata.Messages.INVALID_DATE;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.time.DateService;

/**
 * Offers date translation utilities for {@link MarketDataRequest} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class DateUtils
{
    /**
     * date format to millisecond precision with timezone: <code>yyyyMMdd'T'HHmmssSSSZ</code>
     */
    public static final DateTimeFormatter MILLIS_WITH_TZ = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLISECOND).append(Z).toString()).withZone(ZoneId.systemDefault());
    /**
     * date format to millisecond precision without timezone: <code>yyyyMMdd'T'HHmmssSSS</code>
     */
    public static final DateTimeFormatter MILLIS = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLISECOND).toString()).withZone(DateService.zoneUTC);
    /**
     * date format to second precision with timezone: <code>yyyyMMdd'T'HHmmssZ</code>
     */
    public static final DateTimeFormatter SECONDS_WITH_TZ = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(Z).toString()).withZone(DateService.zoneUTC);
    /**
     * date format to second precision without timezone: <code>yyyyMMdd'T'HHmmss</code>
     */
    public static final DateTimeFormatter SECONDS = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).toString()).withZone(DateService.zoneUTC);
    /**
     * date format to minute precision with timezone: <code>yyyyMMdd'T'HHmmZ</code>
     */
    public static final DateTimeFormatter MINUTES_WITH_TZ = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(Z).toString()).withZone(DateService.zoneUTC);
    /**
     * date format to minute precision without timezone: <code>yyyyMMdd'T'HHmm</code>
     */
    public static final DateTimeFormatter MINUTES =  DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).toString()).withZone(DateService.zoneUTC);
    /**
     * date format to day-of-month precision with timezone: <code>yyyyMMddZ</code>
     */
    public static final DateTimeFormatter DAYS_WITH_TZ = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(Z).toString()).withZone(DateService.zoneUTC);
    /**
     * date format to day-of-month precision without timezone: <code>yyyyMMdd</code>
     */
    public static final DateTimeFormatter DAYS = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).toString()).withZone(DateService.zoneUTC);
    /**
     * date format needed for FIX specified UTCTimestamp type
     */
    public static final DateTimeFormatter FIX = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH).append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toString());
    /**
     * date format needed for FIX specified UTCTimestamp type (with millis)
     */
    public static final DateTimeFormatter FIX_MILLIS = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH).append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toString());
    /**
     * valid date/time formats
     */
    private static final DateTimeFormatter[] dateTimeFormats = new DateTimeFormatter[] {
      FULL_MILLISECONDS,
      MILLIS,
      SECONDS_WITH_TZ,
      SECONDS,
      MINUTES_WITH_TZ,
      MINUTES };
    /**
     * valid date formats
     */
    private static final DateTimeFormatter[] dateFormats = new DateTimeFormatter[] {
      DAYS_WITH_TZ,
      DAYS };
    /**
     * default date pattern
     */
    private static final DateTimeFormatter DEFAULT_FORMAT = MILLIS_WITH_TZ;
    /**
     * Converts the given <code>Date</code> value to a <code>String</code> representation usable with
     * {@link MarketDataRequest} objects.
     * 
     * <p>The format of the returned value is ISO 8601 basic format to millisecond precision with
     * time zone offset.  This format can be expressed as: <code>yyyyMMdd'T'HHmmssSSSZ</code> in terms of the format expected
     * by {@link SimpleDateFormat}.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>String</code> value
     * @deprecated use {@link #dateToString(LocalDateTime)}
     */
    @Deprecated
    public static String dateToString(Date inDate)
    {
        return dateToString(DateService.toLocalDateTime(inDate));
    }
    /**
     * Converts the given <code>LocalDateTime</code> value to a <code>String</code> representation usable with {@link MarketDataRequest} objects.
     *
     * <p>The format of the returned value is ISO 8601 basic format to millisecond precision with
     * time zone offset.  This format can be expressed as: <code>yyyyMMdd'T'HHmmssSSSZ</code> in terms of the format expected
     * by {@link SimpleDateFormat}.
     *
     * @param inDateTime a <code>LocalDateTime</code> value
     * @return a <code>String</code> value
     */
    public static String dateToString(LocalDateTime inDateTime)
    {
        return dateToString(inDateTime,
                            DEFAULT_FORMAT);
    }
    /**
     * Converts the given <code>Date</code> value to a <code>String</code> representation in the given format usable with
     * {@link MarketDataRequest} objects.
     * 
     * @param inDate a <code>Date</code> value
     * @param inFormat a <code>DateTimeFormatter</code> value
     * @return a <code>String</code> value
     * @deprecated use {@link #dateToString(LocalDateTime, DateTimeFormatter)}
     */
    @Deprecated
    public static String dateToString(Date inDate,
                                      DateTimeFormatter inFormat)
    {
        return dateToString(DateService.toLocalDateTime(inDate),
                            inFormat);
    }
    /**
     * Converts the given <code>LocalDateTime</code> value to a <code>String</code> representation in the given format usable with
     * {@link MarketDataRequest} objects.
     * 
     * @param inDateTime a <code>LocalDateTime</code> value
     * @param inFormat a <code>DateTimeFormatter</code> value
     * @return a <code>String</code> value
     */
    public static String dateToString(LocalDateTime inDateTime,
                                      DateTimeFormatter inFormat)
    {
        return inDateTime.format(inFormat);
    }
    /**
     * Parses the given <code>String</code> to a <code>LocalDateTime</code> value.
     * 
     * <p>The given <code>String</code> is expected to be formatted in ISO 8601 basic format as described
     * by {@link DateUtils#dateToString(LocalDateTime)}.  The following formats are accepted:
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
     * @param inDateString a <code>String</code> value a <code>String</code> containing a date value to be parsed.
     * @return a <code>LocalDateTime</code> value 
     * @throws MarketDataRequestException if the given <code>String</code> could not be parsed 
     */
    public static LocalDateTime stringToDate(String inDateString)
            throws MarketDataRequestException
    {
        inDateString = StringUtils.trimToNull(inDateString);
        if(inDateString == null) {
            throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_DATE,
                                                                        inDateString));
        }
        for(DateTimeFormatter formatter : dateTimeFormats) {
            try {
                return LocalDateTime.parse(inDateString,
                                           formatter);
            } catch (DateTimeParseException e) {
                // this format didn't work, try a less specific one
            }
        }
        for(DateTimeFormatter formatter : dateFormats) {
            try {
                return LocalDate.parse(inDateString,
                                       formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                // this format didn't work, try a less specific one
            }
        }
        throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_DATE,
                                                                    inDateString));
    }
}
