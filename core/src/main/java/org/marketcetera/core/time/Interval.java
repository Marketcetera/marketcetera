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

/* $License$ */

/**
 *
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
     * Get the interval value.
     *
     * @return a <code>org.joda.time.Interval</code> value
     */
    public org.joda.time.Interval getInterval()
    {
        return interval;
    }
    /**
     *
     *
     * @param inTime
     * @return
     */
    public boolean contains(DateTime inTime)
    {
        return interval.contains(inTime);
    }
    @PostConstruct
    public void start()
    {
        Validate.isTrue(wallclockPattern.matcher(begin).matches(),
                        "Begin must be of pattern 00:00:00");
        Validate.isTrue(wallclockPattern.matcher(end).matches(),
                        "End must be of pattern 00:00:00");
        DateTime expectedIntervalStart = new DateTime().withTimeAtStartOfDay().plus(WALLCLOCK_SECONDS_LOCAL.parseLocalDateTime(begin).getMillisOfDay());
        DateTime expectedIntervalEnd = new DateTime().withTimeAtStartOfDay().plus(WALLCLOCK_SECONDS_LOCAL.parseLocalDateTime(end).getMillisOfDay());
        Validate.isTrue(expectedIntervalEnd.isAfter(expectedIntervalStart),
                        "End must be after start");
        interval = new org.joda.time.Interval(expectedIntervalStart,
                                              expectedIntervalEnd);
    }
    private static final Pattern wallclockPattern = Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}$");
    private String begin;
    private String end;
    private org.joda.time.Interval interval;
    /**
     * parses offline start and end intervals
     */
    private final DateTimeFormatter WALLCLOCK_SECONDS_LOCAL = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE)
            .append(COLON).append(SECOND).toFormatter();
}
