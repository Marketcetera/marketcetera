package org.marketcetera.core.time;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates time values from the given inputs.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@Component
@ClassVersion("$Id$")
public class TimeFactoryImpl
        implements TimeFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.ramius.inputs.TimeFactory#create(java.lang.String)
     */
    @Override
    public DateTime create(String inValue)
    {
        inValue = StringUtils.trimToNull(inValue);
        Validate.notNull(inValue);
        DateTime value = null;
        for(DateTimeFormatter formatter : FORMATTERS) {
            try {
                value = formatter.parseDateTime(inValue);
            } catch (IllegalArgumentException ignored) {}
        }
        if(value != null &&
           SECONDS_PATTERN.matcher(inValue).matches()) {
            value = value.plus(new LocalDate().toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis());
        }
        Validate.notNull(value);
        return value;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ramius.inputs.TimeFactory#create(long)
     */
    @Override
    public DateTime create(long inValue)
    {
        return new DateTime(inValue).toDateTime(DateTimeZone.UTC);
    }
    private static final Pattern SECONDS_PATTERN = Pattern.compile("^([0-9]{1,2}){0,1}(:[0-9]{1,2}){0,1}(:[0-9]{1,2}){0,1}$");
    public static final DateTimeZone ZONE = DateTimeZone.UTC;
    public static final DateTimeFormatter YEAR = new DateTimeFormatterBuilder().appendYear(4,4).toFormatter();
    public static final DateTimeFormatter MONTH = new DateTimeFormatterBuilder().appendMonthOfYear(2).toFormatter();
    public static final DateTimeFormatter DAY = new DateTimeFormatterBuilder().appendDayOfMonth(2).toFormatter();
    public static final DateTimeFormatter DASH = new DateTimeFormatterBuilder().appendLiteral('-').toFormatter();
    public static final DateTimeFormatter SPACE = new DateTimeFormatterBuilder().appendLiteral(' ').toFormatter();
    public static final DateTimeFormatter T = new DateTimeFormatterBuilder().appendLiteral('T').toFormatter();
    public static final DateTimeFormatter Z = new DateTimeFormatterBuilder().appendLiteral('Z').toFormatter();
    public static final DateTimeFormatter COMMA = new DateTimeFormatterBuilder().appendLiteral(',').toFormatter();
    public static final DateTimeFormatter COLON = new DateTimeFormatterBuilder().appendLiteral(':').toFormatter();
    public static final DateTimeFormatter PERIOD = new DateTimeFormatterBuilder().appendLiteral('.').toFormatter();
    public static final DateTimeFormatter SLASH = new DateTimeFormatterBuilder().appendLiteral('/').toFormatter();
    public static final DateTimeFormatter HOUR = new DateTimeFormatterBuilder().appendHourOfDay(2).toFormatter();
    public static final DateTimeFormatter MINUTE = new DateTimeFormatterBuilder().appendMinuteOfHour(2).toFormatter();
    public static final DateTimeFormatter SECOND = new DateTimeFormatterBuilder().appendSecondOfMinute(2).toFormatter();
    public static final DateTimeFormatter US_DATE = new DateTimeFormatterBuilder().append(DAY).append(SLASH).append(MONTH).append(SLASH).append(YEAR).toFormatter();
    public static final DateTimeFormatter INTL_DATE = new DateTimeFormatterBuilder().append(MONTH).append(SLASH).append(DAY).append(SLASH).append(YEAR).toFormatter();
    public static final DateTimeFormatter MILLISECOND = new DateTimeFormatterBuilder().appendMillisOfSecond(3).toFormatter();
    /**
     * ISO9601 with millis
     */
    public static final DateTimeFormatter FULL_MILLISECONDS_CONDENSED = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLISECOND).append(Z).toFormatter().withZone(ZONE);
    /**
     * full seconds: YYYYMMDD-HH:MM:SS in UTC
     */
    public static final DateTimeFormatter FULL_SECONDS = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH)
            .append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toFormatter().withZone(ZONE);
    /**
     * full seconds: YYYYMMDD-HH:MM:SS.sss in UTC
     */
    public static final DateTimeFormatter FULL_MILLISECONDS = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH)
            .append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toFormatter().withZone(ZONE);
    /**
     * full seconds: YYYYMMDD-HH:MM:SS.sss in local time zone
     */
    public static final DateTimeFormatter FULL_MILLISECONDS_LOCAL = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH)
            .append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toFormatter();
    /**
     * wallclock seconds: HH:MM:SS in UTC
     */
    public static final DateTimeFormatter WALLCLOCK_SECONDS = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toFormatter().withZone(ZONE);
    /**
     * wallclock milliseconds: HH:MM:SS.sss in UTC
     */
    public static final DateTimeFormatter WALLCLOCK_MILLISECONDS = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE)
            .append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toFormatter().withZone(ZONE);
    /**
     * wallclock milliseconds: HH:MM:SS.sss in local time zone
     */
    public static final DateTimeFormatter WALLCLOCK_MILLISECONDS_LOCAL = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE)
            .append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toFormatter();
    /**
     * wallclock minutes: HH:MM
     */
    public static final DateTimeFormatter WALLCLOCK_MINUTES = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE).toFormatter().withZone(ZONE);
    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] { FULL_SECONDS,WALLCLOCK_SECONDS,WALLCLOCK_MINUTES,WALLCLOCK_MILLISECONDS,US_DATE,INTL_DATE,FULL_MILLISECONDS_CONDENSED };
}
