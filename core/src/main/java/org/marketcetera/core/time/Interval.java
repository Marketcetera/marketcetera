package org.marketcetera.core.time;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
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
     * @param inTime a <code>LocalDateTime</code> value
     * @return a <code>boolean</code> value
     */
    public boolean contains(LocalDateTime inTime)
    {
        LocalDateTime expectedIntervalStart = inTime.toLocalDate().atStartOfDay().plusSeconds(beginTime.toSecondOfDay());
        LocalDateTime expectedIntervalEnd = inTime.toLocalDate().atStartOfDay().plusSeconds(endTime.toSecondOfDay());
        // open at the beginning, closed at the end
        boolean result = !expectedIntervalStart.isAfter(inTime) && expectedIntervalEnd.isAfter(inTime);
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
        beginTime = LocalTime.parse(begin,
                                    TimeFactoryImpl.WALLCLOCK_SECONDS_LOCAL);
        endTime = LocalTime.parse(end,
                                  TimeFactoryImpl.WALLCLOCK_SECONDS_LOCAL);
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
     * parsed begin time value
     */
    private LocalTime beginTime;
    /**
     * parsed end time value
     */
    private LocalTime endTime;
    /**
     * interval begin pattern
     */
    private String begin;
    /**
     * interval end pattern
     */
    private String end;
    /**
     * regex for validating wallclock begin and end points
     */
    private static final Pattern wallclockPattern = Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}$");
}
