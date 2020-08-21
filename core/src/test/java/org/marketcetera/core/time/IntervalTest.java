package org.marketcetera.core.time;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;

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
     * Perform various interval tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testIntervalInside()
            throws Exception
    {
        doIntervalTest("00:20:00",
                       "00:30:00",
                       "00:25:00",
                       true);
        doIntervalTest("00:20:00",
                       "00:30:00",
                       "00:20:00",
                       true);
        doIntervalTest("00:20:00",
                       "00:30:00",
                       "00:30:00",
                       false);
        doIntervalTest("00:20:00",
                       "00:30:00",
                       "00:15:00",
                       false);
        doIntervalTest("00:20:00",
                       "00:30:00",
                       "00:45:00",
                       false);
    }
    /**
     * Perform a single interval test with the given attributes.
     *
     * @param inBegin a <code>String</code> value
     * @param inEnd a <code>String</code> value
     * @param inTestString a <code>String</code> value
     * @param inExpectedResult a <code>boolean</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doIntervalTest(String inBegin,
                                String inEnd,
                                String inTestString,
                                boolean inExpectedResult)
            throws Exception
    {
        Interval testInterval = new Interval();
        testInterval.setBegin(inBegin);
        testInterval.setEnd(inEnd);
        testInterval.start();
        LocalDateTime timePoint = LocalDateTime.now().toLocalDate().atStartOfDay().plusSeconds(LocalTime.parse(inTestString,
                                                                                                               TimeFactoryImpl.WALLCLOCK_SECONDS_LOCAL).toSecondOfDay());
        assertEquals("Expected '" + inTestString + "' to be " + (inExpectedResult?"inside":"outside") + " (" + inBegin + "-" + inEnd + "]",
                     inExpectedResult,
                     testInterval.contains(timePoint));
    }
}
