package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.marketdata.OrderBookTest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Tests {@link DepthOfBook}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class DepthOfBookTest
{
    private final MSymbol metc = new MSymbol("METC");
    private final MSymbol goog = new MSymbol("GOOG");
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
        bids = EventBaseTest.generateBidEvents(metc,
                                               exchange1,
                                               10);
        asks = EventBaseTest.generateAskEvents(metc,
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
                new DepthOfBook(null,
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
                new DepthOfBook(bids,
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
                new DepthOfBook(bids,
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
                new DepthOfBook(bids,
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
                new DepthOfBook(bids,
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
                new DepthOfBook(new ArrayList<BidEvent>(),
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
                new DepthOfBook(Arrays.asList(new BidEvent[] { null } ),
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
                new DepthOfBook(bids,
                                Arrays.asList(new AskEvent[] { null } ),
                                new Date(),
                                metc);
            }
        };
        verifyDepthOfBook(new DepthOfBook(new ArrayList<BidEvent>(),
                                          asks,
                                          new Date(),
                                          metc),
                          asks,
                          new ArrayList<BidEvent>());
        verifyDepthOfBook(new DepthOfBook(bids,
                                          new ArrayList<AskEvent>(),
                                          new Date(),
                                          metc),
                          new ArrayList<AskEvent>(),
                          bids);
        verifyDepthOfBook(new DepthOfBook(bids,
                                          asks,
                                          new Date(),
                                          metc),
                          asks,
                          bids);
        // different exchange allowed (supports Level 2 display, e.g.)
        bids.add(EventBaseTest.generateBidEvent(metc,
                                                exchange2));
        asks.add(EventBaseTest.generateAskEvent(metc,
                                                exchange2));
        verifyDepthOfBook(new DepthOfBook(bids,
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
        final DepthOfBook dob = new DepthOfBook(bids,
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
                dob.getBids().add(EventBaseTest.generateBidEvent(metc,
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
                dob.getAsks().add(EventBaseTest.generateAskEvent(metc,
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
        DepthOfBook metc1 = new DepthOfBook(bids,
                                            asks,
                                            new Date(System.currentTimeMillis() - 5000),
                                            metc);
        DepthOfBook metc2 = new DepthOfBook(bids,
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
            googBids.add(new BidEvent(bid.getMessageId(),
                                      bid.getTimeMillis(),
                                      goog,
                                      bid.getExchange(),
                                      bid.getPrice(),
                                      bid.getSize()));
            metcBids.add(new BidEvent(bid.getMessageId(),
                                      bid.getTimeMillis(),
                                      metc,
                                      bid.getExchange(),
                                      bid.getPrice(),
                                      bid.getSize()));
        }
        List<AskEvent> googAsks = new ArrayList<AskEvent>();
        List<AskEvent> metcAsks = new ArrayList<AskEvent>();
        for(AskEvent ask : asks) {
            googAsks.add(new AskEvent(ask.getMessageId(),
                                      ask.getTimeMillis(),
                                      goog,
                                      ask.getExchange(),
                                      ask.getPrice(),
                                      ask.getSize()));
            metcAsks.add(new AskEvent(ask.getMessageId(),
                                      ask.getTimeMillis(),
                                      metc,
                                      ask.getExchange(),
                                      ask.getPrice(),
                                      ask.getSize()));
        }
        DepthOfBook goog1 = new DepthOfBook(googBids,
                                            googAsks,
                                            metc1.getTimestampAsDate(),
                                            goog);
        DepthOfBook metc3 = new DepthOfBook(metcBids,
                                            metcAsks,
                                            metc1.getTimestampAsDate(),
                                            metc);
        assertFalse(metc1.equivalent(goog1));
        assertFalse(metc3.equivalent(goog1));
        assertTrue(metc1.equivalent(metc3));
        // compare a book with empty bids and asks
        assertTrue(new DepthOfBook(new ArrayList<BidEvent>(),
                                   new ArrayList<AskEvent>(),
                                   new Date(),
                                   metc).equivalent(new DepthOfBook(new ArrayList<BidEvent>(),
                                                                    new ArrayList<AskEvent>(),
                                                                    new Date(),
                                                                    metc)));
        // now empty on just one side
        assertFalse(new DepthOfBook(bids,
                                    new ArrayList<AskEvent>(),
                                    new Date(),
                                    metc).equivalent(new DepthOfBook(new ArrayList<BidEvent>(),
                                                                     new ArrayList<AskEvent>(),
                                                                     new Date(),
                                                                     metc)));
        assertFalse(new DepthOfBook(new ArrayList<BidEvent>(),
                                    asks,
                                    new Date(),
                                    metc).equivalent(new DepthOfBook(new ArrayList<BidEvent>(),
                                                                     new ArrayList<AskEvent>(),
                                                                     new Date(),
                                                                     metc)));
        // compare lists that differ by an element aside from the first one
        List<BidEvent> newBids = new ArrayList<BidEvent>(bids);
        newBids.remove(newBids.size()-1);
        newBids.add(EventBaseTest.generateBidEvent(newBids.get(0).getSymbol(),
                                                   newBids.get(0).getExchange()));
        assertEquals(bids.size(),
                     newBids.size());
        assertEquals(bids.get(0),
                     newBids.get(0));
        assertFalse(bids.get(bids.size()-1).equals(newBids.get(newBids.size()-1)));
        // this gives us two bid lists that are the same except for the last element
        DepthOfBook book1 = new DepthOfBook(bids,
                                            asks,
                                            new Date(),
                                            metc);
        DepthOfBook book2 = new DepthOfBook(newBids,
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
    public static void verifyDepthOfBook(DepthOfBook inActualDepthOfBook,
                                         List<AskEvent> inExpectedAsks,
                                         List<BidEvent> inExpectedBids)
        throws Exception
    {
        assertEquals(OrderBookTest.convertEvents(inExpectedAsks),
                     OrderBookTest.convertEvents(inActualDepthOfBook.getAsks()));
        assertEquals(OrderBookTest.convertEvents(inExpectedBids),
                     OrderBookTest.convertEvents(inActualDepthOfBook.getBids()));
        assertNotNull(inActualDepthOfBook.toString());
        List<EventBase> expectedEvents = new LinkedList<EventBase>();
        expectedEvents.addAll(inExpectedAsks);
        expectedEvents.addAll(inExpectedBids);
        AggregateEventTest.verifyDecomposedEvents(inActualDepthOfBook,
                                                  expectedEvents);
    }
}
