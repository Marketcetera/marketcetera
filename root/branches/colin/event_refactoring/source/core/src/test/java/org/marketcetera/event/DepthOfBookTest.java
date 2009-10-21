package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import org.marketcetera.marketdata.OrderBookTest;

/* $License$ */

/**
 * Tests {@link DepthOfBookEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class DepthOfBookTest
{
    /**
     * Verifies the given actual <code>DepthOfBook</code> contains the expected values.
     *
     * @param inActualDepthOfBook a <code>DepthOfBook</code> value
     * @param inExpectedAsks a <code>List&lt;AskEvent&gt;</code> value
     * @param inExpectedBids a <code>List&lt;BidEvent&gt;</code> value
     * @throws Exception if an error occurs
     */
    public static void verifyDepthOfBook(DepthOfBookEvent inActualDepthOfBook,
                                         List<AskEvent> inExpectedAsks,
                                         List<BidEvent> inExpectedBids)
        throws Exception
    {
        assertEquals(OrderBookTest.convertEvents(inExpectedAsks),
                     OrderBookTest.convertEvents(inActualDepthOfBook.getAsks()));
        assertEquals(OrderBookTest.convertEvents(inExpectedBids),
                     OrderBookTest.convertEvents(inActualDepthOfBook.getBids()));
        assertNotNull(inActualDepthOfBook.toString());
        List<Event> expectedEvents = new LinkedList<Event>();
        expectedEvents.addAll(inExpectedAsks);
        expectedEvents.addAll(inExpectedBids);
        AggregateEventTest.verifyDecomposedEvents(inActualDepthOfBook,
                                                  expectedEvents);
    }
}
