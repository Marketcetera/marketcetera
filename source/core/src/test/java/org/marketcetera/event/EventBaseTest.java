package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;

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
     * Random number generator needed for data generation
     */
    private static final Random random = new Random(System.nanoTime());
    /**
     * counter used to guarantee uniqueness
     */
    private static final AtomicLong counter = new AtomicLong(0);
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
        MockEvent e3 = new MockEvent(e2.getTimeMillis());
        Thread.sleep(100);
        MockEvent e4 = new MockEvent();
        assertTrue(e1.getTimeMillis() < e2.getTimeMillis());
        assertTrue(e2.getTimeMillis() == e3.getTimeMillis());
        assertTrue(e3.getTimeMillis() < e4.getTimeMillis());
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
     * Tests construction of events based on {@link EventBase}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void constructor()
        throws Exception
    {
        final long id = System.nanoTime();
        final long timestamp = System.currentTimeMillis();
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockEvent(-1,
                              timestamp);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockEvent(id,
                              -1);
            }
        };
        verifyMockEvent(new MockEvent(id,
                                      timestamp),
                        id,
                        timestamp);
    }
    /**
     * Tests equals and hashcode for objects based on {@link EventBase}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void equalsAndHashcode()
        throws Exception
    {
        long id = System.nanoTime();
        long timestamp = System.currentTimeMillis();
        MockEvent e1 = new MockEvent(id,
                                     timestamp);
        // equals null
        assertFalse(e1.equals(null));
        // wrong class
        assertFalse(e1.equals(this));
        // same
        assertEquals(e1,
                     e1);
        // id varies
        assertFalse(e1.hashCode() == new MockEvent(id + 1,
                                                   timestamp).hashCode());
        // note that inequal objects do not always hash to inequal values
        assertFalse(e1.equals(new MockEvent(id + 1,
                                            timestamp)));
        // timestamp varies
        assertEquals(e1.hashCode(),
                     new MockEvent(id,
                                   timestamp + 1).hashCode());
        assertEquals(e1,
                     new MockEvent(id,
                                   timestamp + 1));
    }
    /**
     * Generates the given number of <code>AskEvent</code> objects with the given attributes.
     * 
     * <p>Unspecified attributes are randomized except for the date, which is set to the
     * current time.  The timestamp is not guaranteed to be unique or consistent for
     * all the events in the list.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @param inCount an <code>int</code> value
     * @return a <code>List&lt;AskEvent&gt;</code> value
     */
    public static List<AskEvent> generateAskEvents(MSymbol inSymbol,
                                                   String inExchange,
                                                   int inCount)
    {
        List<AskEvent> asks = new ArrayList<AskEvent>();
        for(int i=0;i<inCount;i++) {
            asks.add(generateAskEvent(inSymbol,
                                      inExchange));
        }
        return asks;
    }
    /**
     * Generates the given number of <code>BidEvent</code> objects with the given attributes.
     * 
     * <p>Unspecified attributes are randomized except for the date, which is set to the
     * current time.  The timestamp is not guaranteed to be unique or consistent for
     * all the events in the list. 
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @param inCount an <code>int</code> value
     * @return a <code>List&lt;BidEvent&gt;</code> value
     */
    public static List<BidEvent> generateBidEvents(MSymbol inSymbol,
                                                   String inExchange,
                                                   int inCount)
    {
        List<BidEvent> bids = new ArrayList<BidEvent>();
        for(int i=0;i<inCount;i++) {
            bids.add(generateBidEvent(inSymbol,
                                      inExchange));
        }
        return bids;
    }
    /**
     * Generates an {@link AskEvent} using the given symbol and exchange.
     *
     * @param inSymbol a <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateAskEvent(MSymbol inSymbol,
                                            String inExchange)
    {
        return new AskEvent(counter.incrementAndGet(),
                            System.currentTimeMillis(),
                            inSymbol,
                            inExchange,
                            generateDecimalValue(),
                            generateIntegerValue());
    }
    /**
     * Generates an {@link AskEvent} using the given symbol, exchange, and price.
     *
     * @param inSymbol a <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateAskEvent(MSymbol inSymbol,
                                            String inExchange,
                                            BigDecimal inPrice)
    {
        return new AskEvent(counter.incrementAndGet(),
                            System.currentTimeMillis(),
                            inSymbol,
                            inExchange,
                            inPrice,
                            generateIntegerValue());
    }
    /**
     * Generates a {@link BidEvent} using the given symbol and exchange.
     *
     * @param inSymbol a <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateBidEvent(MSymbol inSymbol,
                                            String inExchange)
    {
        return new BidEvent(counter.incrementAndGet(),
                            System.currentTimeMillis(),
                            inSymbol,
                            inExchange,
                            generateDecimalValue(),
                            generateIntegerValue());
    }
    /**
     * Generates a {@link BidEvent} using the given symbol, exchange, and price.
     *
     * @param inSymbol a <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateBidEvent(MSymbol inSymbol,
                                            String inExchange,
                                            BigDecimal inPrice)
    {
        return new BidEvent(counter.incrementAndGet(),
                            System.currentTimeMillis(),
                            inSymbol,
                            inExchange,
                            inPrice,
                            generateIntegerValue());
    }
    /**
     * Generates a {@link TradeEvent} using the given symbol and exchange.
     *
     * @param inSymbol a <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateTradeEvent(MSymbol inSymbol,
                                                String inExchange)
    {
        return new TradeEvent(counter.incrementAndGet(),
                              System.currentTimeMillis(),
                              inSymbol,
                              inExchange,
                              generateDecimalValue(),
                              generateIntegerValue());
    }
    /**
     * Generates a {@link TradeEvent} using the given symbol, exchange, and price.
     *
     * @param inSymbol a <code>MSymbol</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateTradeEvent(MSymbol inSymbol,
                                                String inExchange,
                                                BigDecimal inPrice)
    {
        return new TradeEvent(counter.incrementAndGet(),
                              System.currentTimeMillis(),
                              inSymbol,
                              inExchange,
                              inPrice,
                              generateIntegerValue());
    }
    /**
     * Generates a random value.
     *
     * @return a <code>BigDecimal</code> value
     */
    private static BigDecimal generateDecimalValue()
    {
        return new BigDecimal(String.format("%d.%d",
                                            random.nextInt(10000),
                                            random.nextInt(100)));
    }
    /**
     * Generates a random value.
     *
     * @return a <code>BigDecimal</code> value
     */
    private static BigDecimal generateIntegerValue()
    {
        return new BigDecimal(random.nextInt(10000));
    }
    /**
     * Verifies that the given <code>MockEvent</code> contains the expected values.
     *
     * @param inEvent a <code>MockEvent</code> value
     * @param inExpectedID a <code>long</code> value
     * @param inExpectedTimestamp a <code>long</code> value
     */
    private static void verifyMockEvent(MockEvent inEvent,
                                        long inExpectedID,
                                        long inExpectedTimestamp)
    {
        assertEquals(inExpectedID,
                     inEvent.getMessageId());
        assertEquals(inExpectedTimestamp,
                     inEvent.getTimeMillis());
        assertEquals(new Date(inExpectedTimestamp),
                     inEvent.getTimestampAsDate());
        assertNotNull(inEvent.toString());
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
        private static final long serialVersionUID = 1L;
        /**
         * Create a new MockEvent instance.
         *
         * @param inMessageID a <code>long</code> value containing the id to use
         * @param inTimestamp a <code>long</code> value containing the timestamp to use
         */
        public MockEvent(long inMessageID,
                         long inTimestamp)
        {
            super(inMessageID,
                  inTimestamp);
        }
        public MockEvent(MarketDataRequest inRequest)
        {
            super(System.currentTimeMillis(),
                  System.nanoTime());
            setSource(inRequest);
        }
        /**
         * Create a new MockEvent instance.
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
            this(System.nanoTime(),
                 inTimestamp);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return Long.toString(getTimeMillis());
        }
    }
    /**
     * Compares two events based on their <code>MessageID</code> values.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    public static class MessageIDComparator
        implements Comparator<EventBase>
    {
        /**
         * static instance
         */
        public static final MessageIDComparator instance = new MessageIDComparator();
        /**
         * Create a new MessageIDComparator instance.
         */
        private MessageIDComparator() {}
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(EventBase inO1,
                           EventBase inO2)
        {
            return new Long(inO1.getMessageId()).compareTo(inO2.getMessageId());
        }
    }
}
