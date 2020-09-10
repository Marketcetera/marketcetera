package org.marketcetera.core.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.time.DateService;
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
     * @see org.marketcetera.core.Factory#create()
     */
    @Override
    public LocalDateTime create()
    {
        return LocalDateTime.now();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Factory#create(java.lang.Object)
     */
    @Override
    public LocalDateTime create(LocalDateTime inObject)
    {
        return LocalDateTime.from(inObject);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ramius.inputs.TimeFactory#create(java.lang.String)
     */
    @Override
    public LocalDateTime create(String inValue)
    {
        inValue = StringUtils.trimToNull(inValue);
        Validate.notNull(inValue);
        LocalDateTime value = null;
        for(DateTimeFormatter formatter : FORMATTERS) {
            try {
                value = LocalDateTime.parse(inValue,
                                            formatter);
            } catch (IllegalArgumentException ignored) {}
        }
        if(value != null && SECONDS_PATTERN.matcher(inValue).matches()) {
            value = value.plusNanos(LocalDate.now().atStartOfDay().atZone(ZONE).getNano());
        }
        Validate.notNull(value);
        return value;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ramius.inputs.TimeFactory#create(long)
     */
    @Override
    public LocalDateTime create(long inValue)
    {
        return DateService.toLocalDateTime(inValue);
    }
    private static final Pattern SECONDS_PATTERN = Pattern.compile("^([0-9]{1,2}){0,1}(:[0-9]{1,2}){0,1}(:[0-9]{1,2}){0,1}$");
    public static final ZoneId ZONE = DateService.zoneUTC;
    public static final String YEAR = "yyyy";
    public static final String MONTH = "MM";
    public static final String DAY = "dd";
    public static final String DASH = "-";
    public static final String SPACE = " ";
    public static final String T = "'T'";
    public static final String Z = "'Z'";
    public static final String COMMA = "','";
    public static final String COLON = "':'";
    public static final String PERIOD = "'.'";
    public static final String SLASH = "'/'";
    public static final String HOUR = "HH";
    public static final String MINUTE = "mm";
    public static final String SECOND = "ss";
    public static final String MILLISECOND = "SSS";
    /**
     * US-style date: DD/MM/YY or DD/MM/YYYY
     */
    public static final DateTimeFormatter US_DATE = DateTimeFormatter.ofPattern(new StringBuilder().append(DAY).append(SLASH).append(MONTH).append(SLASH).append(YEAR).toString());
    /**
     * International-style date: MM/DD/YY or MM/DD/YYYY
     */
    public static final DateTimeFormatter INTL_DATE = DateTimeFormatter.ofPattern(new StringBuilder().append(MONTH).append(SLASH).append(DAY).append(SLASH).append(YEAR).toString());
    /**
     * ISO9601 with millis
     */
    public static final DateTimeFormatter FULL_MILLISECONDS_CONDENSED = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR)
        .append(MINUTE).append(SECOND).append(MILLISECOND).append(Z).toString()).withZone(ZONE);
    /**
     * full seconds: YYYYMMDD-HH:MM:SS in UTC
     */
    public static final DateTimeFormatter FULL_SECONDS = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH).append(HOUR).append(COLON)
        .append(MINUTE).append(COLON).append(SECOND).toString()).withZone(ZONE);
    /**
     * full seconds: YYYYMMDD-HH:MM:SS.sss in UTC
     */
    public static final DateTimeFormatter FULL_MILLISECONDS = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH)
            .append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toString()).withZone(ZONE);
    /**
     * full seconds: YYYYMMDD-HH:MM:SS.sss in local time zone
     */
    public static final DateTimeFormatter FULL_MILLISECONDS_LOCAL = DateTimeFormatter.ofPattern(new StringBuilder().append(YEAR).append(MONTH).append(DAY).append(DASH)
            .append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toString());
    /**
     * wallclock seconds: HH:MM:SS in UTC
     */
    public static final DateTimeFormatter WALLCLOCK_SECONDS = DateTimeFormatter.ofPattern(new StringBuilder().append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toString()).withZone(ZONE);
    /**
     * wallclock seconds: HH:MM:SS in local time zone
     */
    public static final DateTimeFormatter WALLCLOCK_SECONDS_LOCAL = DateTimeFormatter.ofPattern(new StringBuilder().append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toString());
    /**
     * wallclock milliseconds: HH:MM:SS.sss in UTC
     */
    public static final DateTimeFormatter WALLCLOCK_MILLISECONDS = DateTimeFormatter.ofPattern(new StringBuilder().append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND)
        .append(PERIOD).append(MILLISECOND).toString()).withZone(ZONE);
    /**
     * wallclock milliseconds: HH:MM:SS.sss in local time zone
     */
    public static final DateTimeFormatter WALLCLOCK_MILLISECONDS_LOCAL = DateTimeFormatter.ofPattern(new StringBuilder().append(HOUR).append(COLON).append(MINUTE)
            .append(COLON).append(SECOND).append(PERIOD).append(MILLISECOND).toString());
    /**
     * wallclock minutes: HH:MM
     */
    public static final DateTimeFormatter WALLCLOCK_MINUTES = DateTimeFormatter.ofPattern(new StringBuilder().append(HOUR).append(COLON).append(MINUTE).toString()).withZone(ZONE);
    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] { FULL_SECONDS,WALLCLOCK_SECONDS,WALLCLOCK_MINUTES,WALLCLOCK_MILLISECONDS,US_DATE,INTL_DATE,FULL_MILLISECONDS_CONDENSED };
}
