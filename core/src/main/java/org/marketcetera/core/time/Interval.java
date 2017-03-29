package org.marketcetera.core.time;

import static org.marketcetera.core.time.TimeFactoryImpl.COLON;
import static org.marketcetera.core.time.TimeFactoryImpl.HOUR;
import static org.marketcetera.core.time.TimeFactoryImpl.MINUTE;
import static org.marketcetera.core.time.TimeFactoryImpl.SECOND;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Interval with a begin and end.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class Interval
{
    /**
     * Get the begin value.
     *
     * @return a <code>String</code> value
     */
    public String getBegin()
    {
        return begin;
    }
    /**
     * Sets the begin value.
     *
     * @param inBegin a <code>String</code> value
     */
    public void setBegin(String inBegin)
    {
        begin = inBegin;
    }
    /**
     * Get the end value.
     *
     * @return a <code>String</code> value
     */
    public String getEnd()
    {
        return end;
    }
    /**
     * Sets the end value.
     *
     * @param inEnd a <code>String</code> value
     */
    public void setEnd(String inEnd)
    {
        end = inEnd;
    }
    /**
     * Indicate if the interval contains the given point in time.
     *
     * @param inTime a <code>DateTime</code> value
     * @return a <code>boolean</code> value
     */
    public boolean contains(DateTime inTime)
    {
        DateTime expectedIntervalStart = inTime.withTimeAtStartOfDay().plus(WALLCLOCK_SECONDS_LOCAL.parseLocalDateTime(begin).getMillisOfDay());
        DateTime expectedIntervalEnd = inTime.withTimeAtStartOfDay().plus(WALLCLOCK_SECONDS_LOCAL.parseLocalDateTime(end).getMillisOfDay());
        org.joda.time.Interval interval = new org.joda.time.Interval(expectedIntervalStart,
                                                                     expectedIntervalEnd);
        boolean result = interval.contains(inTime);
        SLF4JLoggerProxy.debug(this,
                               "{} contains {}: {}",
                               this,
                               inTime,
                               result);
        return result;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.isTrue(wallclockPattern.matcher(begin).matches(),
                        "Begin must be of pattern 00:00:00");
        Validate.isTrue(wallclockPattern.matcher(end).matches(),
                        "End must be of pattern 00:00:00");
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Interval [begin=").append(begin).append(", end=").append(end).append("]");
        return builder.toString();
    }
    /**
     * interval begin pattern
     */
    private String begin;
    /**
     * interval end pattern
     */
    private String end;
    /**
     * parses offline start and end intervals
     */
    private final DateTimeFormatter WALLCLOCK_SECONDS_LOCAL = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE)
            .append(COLON).append(SECOND).toFormatter();
    /**
     * regex for validating wallclock begin and end points
     */
    private static final Pattern wallclockPattern = Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}$");
}
