package org.marketcetera.marketdata;

import static java.math.BigDecimal.TEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBook;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.EventBaseTest;
import org.marketcetera.event.QuantityTuple;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.event.TopOfBook;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Tests {@link OrderBook}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
public class OrderBookTest
{
    /**
     * test symbol
     */
    private final MSymbol symbol = new MSymbol("GOOG");
    /**
     * test order book (reset each test)
     */
    private OrderBook book;
    /**
     * test exchange
     */
    private final String exchange = "TEST";
    /**
     * collection used to track expected values for bids
     */
    private final QuantityTupleList<BidEvent> bids = new QuantityTupleList<BidEvent>();
    /**
     * collection used to track expected values for asks
     */
    private final QuantityTupleList<AskEvent> asks = new QuantityTupleList<AskEvent>();
    /**
     * Run once before all tests.
     *
     * @throws Exception if an error occurs
     */
    @BeforeClass
    public static void once()
        throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Run before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setup()
        throws Exception
    {
        bids.clear();
        asks.clear();
        book = new OrderBook(symbol);
    }
    /**
     * Tests the behavior of {@link OrderBook#equals(Object)} and {@link OrderBook#hashCode()}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void equalsAndHashCode()
        throws Exception
    {
        MSymbol otherSymbol = new MSymbol("YHOO");
        MSymbol duplicateSymbol = new MSymbol("GOOG");
        assertEquals(symbol,
                     duplicateSymbol);
        assertFalse(symbol.equals(otherSymbol));
        OrderBook book1 = new OrderBook(symbol);
        // test easy ones
        assertFalse(book1.equals(null));
        assertFalse(book1.equals(this));
        assertEquals(book1,
                     book1);
        // now, ones with the same class
        OrderBook book2 = new OrderBook(otherSymbol);
        OrderBook book3 = new OrderBook(duplicateSymbol);
        assertFalse(book1.equals(book2));
        assertFalse(book1.hashCode() == book2.hashCode());
        assertFalse(book2.equals(book1));
        assertFalse(book2.hashCode() == book1.hashCode());
        assertEquals(book1,
                     book3);
        assertEquals(book3,
                     book1);
        assertEquals(book1.hashCode(),
                     book3.hashCode());
        assertEquals(book3.hashCode(),
                     book1.hashCode());
    }
    /**
     * Tests the order book constructors.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void bookConstruction()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderBook(null);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderBook(null,
                              1);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderBook(symbol,
                              -2);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OrderBook(symbol,
                              0);
            }
        };
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   new OrderBook(symbol));
        verifyBook(symbol,
                   bids,
                   asks,
                   10,
                   new OrderBook(symbol,
                                 10));
    }
    /**
     * Tests book processing of a series of bids, asks, and trades.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void bookAdds()
        throws Exception
    {
        // bid book and ask book are both empty
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // create an ask
        AskEvent ask1 = EventBaseTest.generateAskEvent(symbol,
                                                       exchange);
        book.process(ask1);
        asks.add(ask1);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // create a new ask of lesser worth (higher price)
        AskEvent ask2 = EventBaseTest.generateAskEvent(symbol,
                                                       exchange,
                                                       ask1.getPrice().add(TEN));
        book.process(ask2);
        asks.add(ask2);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // create a new ask of greater worth (lower price)
        AskEvent ask3 = EventBaseTest.generateAskEvent(symbol,
                                                       exchange,
                                                       ask1.getPrice().subtract(TEN));
        book.process(ask3);
        asks.add(ask3);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // add a bid
        BidEvent bid1 = EventBaseTest.generateBidEvent(symbol,
                                                       exchange);
        book.process(bid1);
        bids.add(bid1);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // create a new bid of lesser worth (lower price)
        BidEvent bid2 = EventBaseTest.generateBidEvent(symbol,
                                                       exchange,
                                                       bid1.getPrice().subtract(TEN));
        book.process(bid2);
        bids.add(bid2);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // create a new bid of greater worth (higher price)
        BidEvent bid3 = EventBaseTest.generateBidEvent(symbol,
                                                       exchange,
                                                       bid1.getPrice().add(TEN));
        book.process(bid3);
        bids.add(bid3);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
    }
    /**
     * Tests the order book's ability to correctly process event change and delete actions.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void bookChangesAndDeletes()
        throws Exception
    {
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // add an ask
        AskEvent ask1 = EventBaseTest.generateAskEvent(symbol,
                                                       exchange);
        asks.add(ask1);
        book.process(ask1);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        BidEvent bid1 = EventBaseTest.generateBidEvent(symbol,
                                                       exchange);
        bids.add(bid1);
        book.process(bid1);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // delete each event in turn
        AskEvent ask1Killer = AskEvent.deleteEvent(ask1);
        asks.clear();
        book.process(ask1Killer);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        BidEvent bid1Killer = BidEvent.deleteEvent(bid1);
        bids.clear();
        book.process(bid1Killer);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // try to delete from empty book (matching bid/ask doesn't exist)
        book.process(ask1Killer);
        book.process(bid1Killer);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // add the bid and ask back
        book.process(ask1);
        book.process(bid1);
        asks.add(ask1);
        bids.add(bid1);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // change the ask
        AskEvent askChange = AskEvent.changeEvent(ask1,
                                                  ask1.getTimeMillis(),
                                                  ask1.getSize().add(TEN));
        asks.clear();
        asks.add(askChange);
        book.process(askChange);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // change the bid
        BidEvent bidChange = BidEvent.changeEvent(bid1,
                                                  bid1.getTimeMillis(),
                                                  bid1.getSize().add(TEN));
        bids.clear();
        bids.add(bidChange);
        book.process(bidChange);
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        // create changes for non-existent events
        AskEvent unusedAsk = EventBaseTest.generateAskEvent(symbol,
                                                            exchange);
        book.process(AskEvent.changeEvent(unusedAsk,
                                          unusedAsk.getTimeMillis(),
                                          unusedAsk.getSize().add(TEN)));
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
        BidEvent unusedBid = EventBaseTest.generateBidEvent(symbol,
                                                            exchange);
        book.process(BidEvent.changeEvent(unusedBid,
                                          unusedBid.getTimeMillis(),
                                          unusedBid.getSize().add(TEN)));
        verifyBook(symbol,
                   bids,
                   asks,
                   OrderBook.UNLIMITED_DEPTH,
                   book);
    }
    /**
     * Tests bad values passed to {@link OrderBook#process(QuoteEvent)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void processBadEvents()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                book.process(null);
            }
        };
        final AskEvent badAsk = EventBaseTest.generateAskEvent(new MSymbol("METC"),
                                                               exchange);
        assertFalse(badAsk.getSymbol().equals(symbol));
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                book.process(badAsk);
            }
        };
    }
    /**
     * Tests the behavior of order books with a defined maximum depth.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void bookDepths()
        throws Exception
    {
        // events removed from a book are oldest not best, keep that in mind
        book = new OrderBook(symbol,
                             2);
        // create three asks with a measurable difference in their timestamps
        // to prove that the pruning technique is by age instead of value, make the oldest ask the best (lowest price)
        AskEvent ask1 = EventBaseTest.generateAskEvent(symbol,
                                                       exchange);
        Thread.sleep(250);
        AskEvent ask2 = EventBaseTest.generateAskEvent(symbol,
                                                       exchange,
                                                       ask1.getPrice().add(TEN));
        Thread.sleep(250);
        AskEvent ask3 = EventBaseTest.generateAskEvent(symbol,
                                                       exchange,
                                                       ask2.getPrice().add(TEN));
        // add the ask events to the book
        book.process(ask1);
        asks.add(ask1);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
        book.process(ask2);
        asks.add(ask2);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
        // so far, so good, the order book is now at max size
        // add the third ask, which will trigger the pruning algorithm
        // the pruned event will be the oldest (ask1) not the worst (ask3)
        book.process(ask3);
        asks.add(ask3);
        asks.remove(ask1);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
        // verify the same behavior for bids
        BidEvent bid1 = EventBaseTest.generateBidEvent(symbol,
                                                       exchange);
        Thread.sleep(250);
        BidEvent bid2 = EventBaseTest.generateBidEvent(symbol,
                                                       exchange,
                                                       bid1.getPrice().subtract(TEN));
        Thread.sleep(250);
        BidEvent bid3 = EventBaseTest.generateBidEvent(symbol,
                                                       exchange,
                                                       bid2.getPrice().subtract(TEN));
        // add the bid events to the book
        book.process(bid1);
        bids.add(bid1);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
        book.process(bid2);
        bids.add(bid2);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
        // so far, so good, the order book is now at max size
        // add the third Bid, which will trigger the pruning algorithm
        // the pruned event will be the oldest (Bid1) not the worst (Bid3)
        book.process(bid3);
        bids.add(bid3);
        bids.remove(bid1);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
        // verify deletion behavior on a limited-size book
        AskEvent askKiller = AskEvent.deleteEvent(ask2);
        asks.remove(ask2);
        book.process(askKiller);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
        BidEvent bidKiller = BidEvent.deleteEvent(bid2);
        bids.remove(bid2);
        book.process(bidKiller);
        verifyBook(symbol,
                   bids,
                   asks,
                   2,
                   book);
    }
    /**
     * Verifies that the given {@link OrderBook} contains the given expected values.
     *
     * @param inExpectedSymbol
     * @param inExpectedBids
     * @param inExpectedAsks
     * @param inExpectedMaxDepth
     * @param inActualBook
     * @throws Exception
     */
    private void verifyBook(MSymbol inExpectedSymbol,
                            QuantityTupleList<BidEvent> inExpectedBids,
                            QuantityTupleList<AskEvent> inExpectedAsks,
                            int inExpectedMaxDepth,
                            OrderBook inActualBook)
        throws Exception
    {
        inExpectedAsks.sort(QuantityTuple.PriceComparator.ASCENDING);
        inExpectedBids.sort(QuantityTuple.PriceComparator.DESCENDING);
        List<QuantityTuple> convertedBids = convertEvents(inActualBook.getBidBook());
        List<QuantityTuple> convertedAsks = convertEvents(inActualBook.getAskBook());
        assertEquals(inExpectedSymbol,
                     inActualBook.getSymbol());
        assertEquals(inExpectedBids.getList(),
                     convertedBids);
        assertEquals(inExpectedAsks.getList(),
                     convertedAsks);
        assertEquals(inExpectedMaxDepth,
                     inActualBook.getMaxDepth());
        TopOfBook top = inActualBook.getTopOfBook();
        DepthOfBook depth = inActualBook.getDepthOfBook();
        List<QuantityTuple> convertedDepthBids = convertEvents(depth.getBids());
        List<QuantityTuple> convertedDepthAsks = convertEvents(depth.getAsks());
        if(inExpectedBids.isEmpty()) {
            assertNull(top.getBid());
            assertTrue(depth.getBids().isEmpty());
        } else {
            assertEquals(inExpectedBids.get(0),
                         convertEvent(top.getBid()));
            assertEquals(inExpectedBids.getList(),
                         convertedDepthBids);
        }
        if(inExpectedAsks.isEmpty()) {
            assertNull(top.getAsk());
            assertTrue(depth.getAsks().isEmpty());
        } else {
            assertEquals(inExpectedAsks.get(0),
                         convertEvent(top.getAsk()));
            assertEquals(inExpectedAsks.getList(),
                         convertedDepthAsks);
        }
    }
    /**
     * Converts the given {@link QuoteEvent} values to {@link QuantityTuple} values.
     *
     * @param inEvents a <code>List&lt;? extends EventBase&gt;</code> value
     * @return a <code>List&lt;QuantityTuple&gt;</code>value
     */
    public static List<QuantityTuple> convertEvents(List<? extends EventBase> inEvents)
    {
        List<QuantityTuple> result = new ArrayList<QuantityTuple>();
        for(EventBase event : inEvents) {
            if(event instanceof SymbolExchangeEvent) {
                result.add(convertEvent((SymbolExchangeEvent)event));
            }
        }
        return result;
    }
    /**
     * Convert the given {@link SymbolExchangeEvent} to a {@link QuantityTuple} value.
     *
     * @param inEvent a <code>SymbolExchangeEvent</code> value
     * @return a <code>QuantityTuple</code> value
     */
    public static QuantityTuple convertEvent(SymbolExchangeEvent inEvent)
    {
        if(inEvent == null) {
            return null;
        }
        return new QuantityTuple(inEvent.getPrice(),
                                 inEvent.getSize(),
                                 inEvent.getClass());
    }
    /**
     * Wrapper around a {@link QuantityTuple} {@link List} that accepts {@link QuoteEvent} inputs.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    private static class QuantityTupleList<E extends QuoteEvent>
    {
        /**
         * the list of quantitytuple values
         */
        private final List<QuantityTuple> tuples = new ArrayList<QuantityTuple>();
        /**
         * Adds an event to the list.
         *
         * @param inEvent an <code>E</code> value
         */
        private void add(E inEvent)
        {
            tuples.add(convertEvent(inEvent));
        }
        /**
         * Removes the given <code>E</code> value from the list.
         *
         * @param inEvent an <code>E</code> value
         */
        private void remove(E inEvent)
        {
            tuples.remove(convertEvent(inEvent));
        }
        /**
         * Sorts the list with the given <code>Comparator</code>. 
         *
         * @param inComparator a <code>Comparator&lt;QuantityTuple&gt;</code> value
         */
        private void sort(Comparator<QuantityTuple> inComparator)
        {
            Collections.sort(tuples,
                             inComparator);
        }
        /**
         * Gets the <code>QuantityTuple</code> at the specified index.
         *
         * @param inIndex an <code>int</code> value
         * @return a <code>QuantityTuple</code> value
         */
        private QuantityTuple get(int inIndex)
        {
            return tuples.get(inIndex);
        }
        /**
         * Indicates if the list is empty or not.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isEmpty()
        {
            return tuples.isEmpty();
        }
        /**
         * Clears all objects from the list.
         */
        private void clear()
        {
            tuples.clear();
        }
        /**
         * Returns the list. 
         *
         * @return a <code>List&lt;QuantityTuple&gt;</code> value
         */
        private List<QuantityTuple> getList()
        {
            return tuples;
        }
    }
}
