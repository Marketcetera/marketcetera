package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.OrderBookTest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;

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
    private final Equity metc = new Equity("METC");
    private final Equity goog = new Equity("GOOG");
    private final String exchange1 = "TEST1";
    private final String exchange2 = "TEST2";
    private List<BidEvent> bids;
    private List<AskEvent> asks;
    /**
     * Executed before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setup()
        throws Exception
    {
        bids = EventTestBase.generateEquityBidEvents(metc,
                                                     exchange1,
                                                     10);
        asks = EventTestBase.generateEquityAskEvents(metc,
                                                     exchange1,
                                                     10);
    }
    /**
     * Tests construction of <code>DepthOfBook</code> objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void constructor()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                EventTestBase.generateEquityDepthOfBookEvent(null,
                                                             asks,
                                                             new Date(),
                                                             metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                             null,
                                                             new Date(),
                                                             metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                             asks,
                                                             null,
                                                             metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                             asks,
                                                             new Date(),
                                                             null);
            }
        };
        // wrong symbol (bids)
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                             asks,
                                                             new Date(),
                                                             goog);
            }
        };
        // wrong symbol (asks)
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                EventTestBase.generateEquityDepthOfBookEvent(new ArrayList<BidEvent>(),
                                                             asks,
                                                             new Date(),
                                                             goog);
            }
        };
        // list contains null (bids)
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception

            {
                List<BidEvent> nullBids = new ArrayList<BidEvent>();
                nullBids.add(null);
                EventTestBase.generateEquityDepthOfBookEvent(nullBids,
                                                             asks,
                                                             new Date(),
                                                             metc);
            }
        };
        // list contains null (asks)
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                List<AskEvent> nullAsks = new ArrayList<AskEvent>();
                nullAsks.add(null);
                EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                             nullAsks,
                                                             new Date(),
                                                             metc);
            }
        };
        verifyDepthOfBook(EventTestBase.generateEquityDepthOfBookEvent(new ArrayList<BidEvent>(),
                                                                       asks,
                                                                       new Date(),
                                                                       metc),
                          asks,
                          new ArrayList<BidEvent>());
        verifyDepthOfBook(EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                       new ArrayList<AskEvent>(),
                                                                       new Date(),
                                                                       metc),
                          new ArrayList<AskEvent>(),
                          bids);
        verifyDepthOfBook(EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                       asks,
                                                                       new Date(),
                                                                       metc),
                          asks,
                          bids);
        // different exchange allowed (supports Level 2 display, e.g.)
        bids.add(EventTestBase.generateEquityBidEvent(metc,
                                                      exchange2));
        asks.add(EventTestBase.generateEquityAskEvent(metc,
                                                      exchange2));
        verifyDepthOfBook(EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                       asks,
                                                                       new Date(),
                                                                       metc),
                          asks,
                          bids);
    }
    /**
     * Tests that the values returned for the event lists are immutable.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void immutability()
        throws Exception
    {
        final DepthOfBookEvent dob = EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                                  asks,
                                                                                  new Date(),
                                                                                  metc);
        verifyDepthOfBook(dob,
                          asks,
                          bids);
        new ExpectedFailure<UnsupportedOperationException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                dob.getBids().clear();
            }
        };
        new ExpectedFailure<UnsupportedOperationException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                dob.getBids().add(EventTestBase.generateEquityBidEvent(metc,
                                                                       exchange1));
            }
        };
        new ExpectedFailure<UnsupportedOperationException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                dob.getAsks().clear();
            }
        };
        new ExpectedFailure<UnsupportedOperationException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                dob.getAsks().add(EventTestBase.generateEquityAskEvent(metc,
                                                                       exchange1));
            }
        };
        verifyDepthOfBook(dob,
                          asks,
                          bids);
    }
    /**
     * Tests the ability to compare two <code>DepthOfBook</code> objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void equivalent()
        throws Exception
    {
        // two books with the same symbol, events, but different dates
        DepthOfBookEvent metc1 = EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                              asks,
                                                                              new Date(System.currentTimeMillis() - 5000),
                                                                              metc);
        DepthOfBookEvent metc2 = EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                              asks,
                                                                              new Date(System.currentTimeMillis() + 5000),
                                                                              metc);
        assertTrue(metc1.getTimeMillis() != metc2.getTimeMillis());
        assertTrue(metc1.equivalent(metc1));
        assertTrue(metc1.equivalent(metc2));
        assertFalse(metc1.equivalent(null));
        // create a book that is the same as metc1 but for goog (same bid/ask size for each)
        // we'll also create a third metc book at the same time to prove the methodology
        List<BidEvent> googBids = new ArrayList<BidEvent>();
        List<BidEvent> metcBids = new ArrayList<BidEvent>();
        for(BidEvent bid : bids) {
            googBids.add(EventTestBase.generateEquityBidEvent(bid.getMessageId(),
                                                              bid.getTimeMillis(),
                                                              goog,
                                                              bid.getExchange(),
                                                              bid.getPrice(),
                                                              bid.getSize()));
            metcBids.add(EventTestBase.generateEquityBidEvent(bid.getMessageId(),
                                                              bid.getTimeMillis(),
                                                              metc,
                                                              bid.getExchange(),
                                                              bid.getPrice(),
                                                              bid.getSize()));
        }
        List<AskEvent> googAsks = new ArrayList<AskEvent>();
        List<AskEvent> metcAsks = new ArrayList<AskEvent>();
        for(AskEvent ask : asks) {
            googAsks.add(EventTestBase.generateEquityAskEvent(ask.getMessageId(),
                                                              ask.getTimeMillis(),
                                                              goog,
                                                              ask.getExchange(),
                                                              ask.getPrice(),
                                                              ask.getSize()));
            metcAsks.add(EventTestBase.generateEquityAskEvent(ask.getMessageId(),
                                                              ask.getTimeMillis(),
                                                              metc,
                                                              ask.getExchange(),
                                                              ask.getPrice(),
                                                              ask.getSize()));
        }
        DepthOfBookEvent goog1 = EventTestBase.generateEquityDepthOfBookEvent(googBids,
                                                                                      googAsks,
                                                                                      metc1.getTimestamp(),
                                                                                      goog);
        DepthOfBookEvent metc3 = EventTestBase.generateEquityDepthOfBookEvent(metcBids,
                                                                                      metcAsks,
                                                                                      metc1.getTimestamp(),
                                                                                      metc);
        assertFalse(metc1.equivalent(goog1));
        assertFalse(metc3.equivalent(goog1));
        assertTrue(metc1.equivalent(metc3));
        // compare a book with empty bids and asks
        assertTrue(EventTestBase.generateEquityDepthOfBookEvent(new ArrayList<BidEvent>(),
                                                                new ArrayList<AskEvent>(),
                                                                new Date(),
                                                                metc).equivalent(EventTestBase.generateEquityDepthOfBookEvent(new ArrayList<BidEvent>(),
                                                                                                                              new ArrayList<AskEvent>(),
                                                                                                                              new Date(),
                                                                                                                              metc)));
        // now empty on just one side
        assertFalse(EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                 new ArrayList<AskEvent>(),
                                                                 new Date(),
                                                                 metc).equivalent(EventTestBase.generateEquityDepthOfBookEvent(new ArrayList<BidEvent>(),
                                                                                                                               new ArrayList<AskEvent>(),
                                                                                                                               new Date(),
                                                                                                                               metc)));
        assertFalse(EventTestBase.generateEquityDepthOfBookEvent(new ArrayList<BidEvent>(),
                                    asks,
                                    new Date(),
                                    metc).equivalent(EventTestBase.generateEquityDepthOfBookEvent(new ArrayList<BidEvent>(),
                                                                                                  new ArrayList<AskEvent>(),
                                                                                                  new Date(),
                                                                                                  metc)));
        // compare lists that differ by an element aside from the first one
        List<BidEvent> newBids = new ArrayList<BidEvent>(bids);
        newBids.remove(newBids.size()-1);
        newBids.add(EventTestBase.generateEquityBidEvent((Equity)newBids.get(0).getInstrument(),
                                                         newBids.get(0).getExchange()));
        assertEquals(bids.size(),
                     newBids.size());
        assertEquals(bids.get(0),
                     newBids.get(0));
        assertFalse(bids.get(bids.size()-1).equals(newBids.get(newBids.size()-1)));
        // this gives us two bid lists that are the same except for the last element
        DepthOfBookEvent book1 = EventTestBase.generateEquityDepthOfBookEvent(bids,
                                                                              asks,
                                                                              new Date(),
                                                                              metc);
        DepthOfBookEvent book2 = EventTestBase.generateEquityDepthOfBookEvent(newBids,
                                                                              asks,
                                                                              new Date(),
                                                                              metc);
        assertFalse(book1.equivalent(book2));
    }
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
