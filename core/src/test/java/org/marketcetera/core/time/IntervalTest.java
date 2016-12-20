package org.marketcetera.core.time;

import static org.junit.Assert.assertTrue;
import static org.marketcetera.core.time.TimeFactoryImpl.COLON;
import static org.marketcetera.core.time.TimeFactoryImpl.HOUR;
import static org.marketcetera.core.time.TimeFactoryImpl.MINUTE;
import static org.marketcetera.core.time.TimeFactoryImpl.SECOND;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Test;
import org.marketcetera.core.time.Interval;

/* $License$ */

/**
 * Test {@link Interval} behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class IntervalTest
{
    /**
     * Test that an interval contains a time point tomorrow at the same time.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testIntervalTomorrow()
            throws Exception
    {
        Interval interval1 = new Interval();
        interval1.setBegin("00:20:00");
        interval1.setEnd("00:30:00");
        interval1.start();
        DateTime timePoint1 = new DateTime().plusDays(1).withTimeAtStartOfDay().plus(WALLCLOCK_SECONDS_LOCAL.parseLocalDateTime("00:25:00").getMillisOfDay());
        assertTrue(interval1.contains(timePoint1));
    }
    /**
     * time/date formatter used for test data
     */
    private final DateTimeFormatter WALLCLOCK_SECONDS_LOCAL = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE)
            .append(COLON).append(SECOND).toFormatter();
}
