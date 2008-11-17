package org.marketcetera.event;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/* $License$ */

/**
 * Tests {@link EventBase} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
public class EventBaseTest
{
    /**
     * Tests {@link EventBase.BookAgeComparator#OldestToNewestComparator} and {@link EventBase.BookAgeComparator#NewestToOldestComparator}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void ageComparator()
        throws Exception
    {
        MockEvent e1 = new MockEvent();
        Thread.sleep(100);
        MockEvent e2 = new MockEvent();
        MockEvent e3 = new MockEvent(e2.getTimestamp());
        Thread.sleep(100);
        MockEvent e4 = new MockEvent();
        assertTrue(e1.getTimestamp() < e2.getTimestamp());
        assertTrue(e2.getTimestamp() == e3.getTimestamp());
        assertTrue(e3.getTimestamp() < e4.getTimestamp());
        List<MockEvent> sortedEvents = new ArrayList<MockEvent>();
        sortedEvents.add(e4);
        sortedEvents.add(e2);
        sortedEvents.add(e1);
        sortedEvents.add(e3);
        Collections.sort(sortedEvents,
                         EventBase.BookAgeComparator.OldestToNewestComparator);
        List<MockEvent> expectedResults = new ArrayList<MockEvent>();
        expectedResults.add(e1);
        expectedResults.add(e2);
        expectedResults.add(e3);
        expectedResults.add(e4);
        assertTrue("Expected " + Arrays.toString(expectedResults.toArray()) + " but got " + Arrays.toString(sortedEvents.toArray()), //$NON-NLS-1$ //$NON-NLS-2$
                   Arrays.equals(expectedResults.toArray(),
                                 sortedEvents.toArray()));
        expectedResults.clear();
        // the odd order is due to the fact that two timestamps are equal
        expectedResults.add(e4);
        expectedResults.add(e2);
        expectedResults.add(e3);
        expectedResults.add(e1);
        Collections.sort(sortedEvents,
                         EventBase.BookAgeComparator.NewestToOldestComparator);
        assertTrue("Expected " + Arrays.toString(expectedResults.toArray()) + " but got " + Arrays.toString(sortedEvents.toArray()), //$NON-NLS-1$ //$NON-NLS-2$
                   Arrays.equals(expectedResults.toArray(),
                                 sortedEvents.toArray()));
    }
    /**
     * An event with no additional behavior.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    public static class MockEvent
        extends EventBase
    {
        /**
         * Create a new MockEvent instance.
         *
         * @param inMessageId
         * @param inTimestamp
         */
        public MockEvent()
        {
            this(System.currentTimeMillis());
        }
        /**
         * Create a new MockEvent instance.
         *
         * @param inTimestamp a <code>long</code> value containing the timestamp to use
         */
        public MockEvent(long inTimestamp)
        {
            super(System.nanoTime(),
                  inTimestamp);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return Long.toString(getTimestamp());
        }
    }
}
