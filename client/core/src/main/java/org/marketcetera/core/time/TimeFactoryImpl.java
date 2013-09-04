package org.marketcetera.core.time;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/* $License$ */

/**
 * Creates time values from the given inputs.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TimeFactoryImpl.java 83355 2013-06-26 18:21:12Z colin $
 * @since $Release$
 */
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
            value = value.plus(new DateMidnight(DateTimeZone.UTC).getMillis());
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
    private static final DateTimeZone ZONE = DateTimeZone.UTC;
    private static final DateTimeFormatter YEAR = new DateTimeFormatterBuilder().appendYear(4,4).toFormatter();
    private static final DateTimeFormatter MONTH = new DateTimeFormatterBuilder().appendMonthOfYear(2).toFormatter();
    private static final DateTimeFormatter DAY = new DateTimeFormatterBuilder().appendDayOfMonth(2).toFormatter();
    private static final DateTimeFormatter DASH = new DateTimeFormatterBuilder().appendLiteral('-').toFormatter();
    private static final DateTimeFormatter COLON = new DateTimeFormatterBuilder().appendLiteral(':').toFormatter();
    private static final DateTimeFormatter HOUR = new DateTimeFormatterBuilder().appendHourOfDay(2).toFormatter();
    private static final DateTimeFormatter MINUTE = new DateTimeFormatterBuilder().appendMinuteOfHour(2).toFormatter();
    private static final DateTimeFormatter SECOND = new DateTimeFormatterBuilder().appendSecondOfMinute(2).toFormatter();
    /**
     * full seconds: YYYYMMDD-HH:MM:SS
     */
    public static final DateTimeFormatter FULL_SECONDS = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH)
            .append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toFormatter().withZone(ZONE);
    /**
     * wallclock seconds: HH:MM:SS
     */
    private static final DateTimeFormatter WALLCLOCK_SECONDS = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE)
            .append(COLON).append(SECOND).toFormatter().withZone(ZONE);
    /**
     * wallclock minutes: HH:MM
     */
    private static final DateTimeFormatter WALLCLOCK_MINUTES = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE)
            .toFormatter().withZone(ZONE);
    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] { FULL_SECONDS,WALLCLOCK_SECONDS,WALLCLOCK_MINUTES };
}
