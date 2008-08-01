package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidAskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.AbstractEventTranslatorTest.QuantityTuple;
import org.marketcetera.event.BidAskEvent.Action;

import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

/* $License$ */

/**
 * Tests {@link OrderBook}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
public class OrderBookTest
{
    private final MSymbol mSymbol = new MSymbol("GOOG"); //$NON-NLS-1$
    
    @Test
    public void equalsAndHashCode()
        throws Exception
    {
        MSymbol otherSymbol = new MSymbol("YHOO"); //$NON-NLS-1$
        MSymbol duplicateSymbol = new MSymbol("GOOG"); //$NON-NLS-1$
        assertEquals(mSymbol,
                     duplicateSymbol);
        assertFalse(mSymbol.equals(otherSymbol));
        OrderBook book1 = new OrderBook(mSymbol);
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
    @Test
    public void nullConstructor()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                new OrderBook(null);
            }
        }.run();
    }
    @Test
    public void depthOfBook()
        throws Exception
    {
        final OrderBook book = new OrderBook(mSymbol);
        doWrongSymbolTest(book);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[0]),
                       Arrays.asList(new QuantityTuple[0]),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        // add a trade early on just to make sure the trade doesn't appear in any d-o-b messages
        book.processEvent(new TradeEvent(issueID(),
                                         System.currentTimeMillis(),
                                         mSymbol.getBaseSymbol(),
                                         "exchange", //$NON-NLS-1$
                                         new BigDecimal(100),
                                         new BigDecimal(50)));
        // add good bid (checkEvent does not throw, add bid, add bid to empty book)
        QuantityTuple bid1Tuple = new QuantityTuple(new BigDecimal("100"), //$NON-NLS-1$
                                                    new BigDecimal("1")); //$NON-NLS-1$
        BidEvent bid1 = new BidEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     bid1Tuple.getPrice(),
                                     bid1Tuple.getSize());
        // before adding the bid, test removing from and changing an empty bid book
        book.processEvent(convertEvent(bid1,
                                       Action.DELETE));
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        book.processEvent(convertEvent(bid1,
                                       Action.CHANGE));
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        // now add a bid
        book.processEvent(bid1);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        // add duplicate bid (add duplicate bid)
        BidEvent bid1Duplicate = new BidEvent(bid1.getMessageId(),
                                              System.currentTimeMillis(),
                                              mSymbol.getBaseSymbol(),
                                              "exchange", //$NON-NLS-1$
                                              bid1Tuple.getPrice(),
                                              bid1Tuple.getSize());
        book.processEvent(bid1Duplicate);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        // add another bid (add bid to non-empty book, add non-duplicate bid)
        QuantityTuple bid2Tuple = new QuantityTuple(new BigDecimal("200"), //$NON-NLS-1$
                                                    new BigDecimal("1")); //$NON-NLS-1$
        
        // (bid2 is better than bid1)
        BidEvent bid2 = new BidEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     bid2Tuple.getPrice(),
                                     bid2Tuple.getSize());
        book.processEvent(bid2);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        // add a bid between bid1 and bid2 (bid 1.5?)
        QuantityTuple bid3Tuple = new QuantityTuple(new BigDecimal("150"), //$NON-NLS-1$
                                                    new BigDecimal("1"));         //$NON-NLS-1$
        // (bid2 is better than bid3 and bid1)
        BidEvent bid3 = new BidEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     bid3Tuple.getPrice(),
                                     bid3Tuple.getSize());
        book.processEvent(bid3);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid3Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        // add ask (add non-bid, add ask, add ask to empty book)
        QuantityTuple ask1Tuple = new QuantityTuple(new BigDecimal("1500"), //$NON-NLS-1$
                                                    new BigDecimal("10")); //$NON-NLS-1$
        
        AskEvent ask1 = new AskEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     ask1Tuple.getPrice(),
                                     ask1Tuple.getSize());
        // before adding the ask, test removing from an empty ask book
        book.processEvent(convertEvent(ask1,
                                       Action.DELETE));
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid3Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        // check change, too
        book.processEvent(convertEvent(ask1,
                                       Action.CHANGE));
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid3Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getDepthOfBook());
        book.processEvent(ask1);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid3Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // add duplicate ask (add duplicate ask)
        AskEvent ask1Duplicate = new AskEvent(ask1.getMessageId(),
                                              System.currentTimeMillis(),
                                              mSymbol.getBaseSymbol(),
                                              "exchange", //$NON-NLS-1$
                                              ask1Tuple.getPrice(),
                                              ask1Tuple.getSize());
        book.processEvent(ask1Duplicate);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid3Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // add a better ask (add ask to non-empty book, add non-duplicate ask)
        QuantityTuple ask2Tuple = new QuantityTuple(new BigDecimal("1000"), //$NON-NLS-1$
                                                    new BigDecimal("10")); //$NON-NLS-1$
        
        AskEvent ask2 = new AskEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     ask2Tuple.getPrice(),
                                     ask2Tuple.getSize());
        book.processEvent(ask2);
        // check contents of book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid3Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // add an equal ask (but with different timestamps)
        AskEvent ask3 = new AskEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     ask2Tuple.getPrice(),
                                     ask2Tuple.getSize());
        book.processEvent(ask3);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid3Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple, ask1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // start with some removes
        book.processEvent(convertEvent(bid3,
                                       Action.DELETE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple, ask1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // try removing the same bid again
        book.processEvent(convertEvent(bid3,
                                       Action.DELETE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple, ask1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // remove an ask
        book.processEvent(convertEvent(ask1,
                                       Action.DELETE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // remove the same ask again
        book.processEvent(convertEvent(ask1,
                                       Action.DELETE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // test some changes
        // change an event not in the book
        book.processEvent(convertEvent(ask1,
                                       Action.CHANGE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        book.processEvent(convertEvent(bid3,
                                       Action.CHANGE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid2Tuple, bid1Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // change an event in the book
        // change bid1 to be a better bid than bid2
        QuantityTuple bid4Tuple = new QuantityTuple(new BigDecimal(bid2Tuple.getPrice().add(new BigDecimal(1)).toString()),
                                                    new BigDecimal("10")); //$NON-NLS-1$
        // create a change for bid2 using the new quantities
        book.processEvent(convertEvent(bid1,
                                       bid4Tuple,
                                       Action.CHANGE));
        // note that bid1 (using bid4tuple) and bid2 have swapped places on the book because of the change
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid4Tuple, bid2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask2Tuple, ask2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // change an ask in the same way (making the ask smaller than the previous)
        QuantityTuple ask3Tuple = new QuantityTuple(new BigDecimal(ask2Tuple.getPrice().subtract(new BigDecimal(1)).toString()),
                                                    ask2Tuple.getSize());
        book.processEvent(convertEvent(ask3,
                                       ask3Tuple,
                                       Action.CHANGE));
        // note that ask3 (using ask3tuple) and ask2 have swapped places on the book because of the change
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bid4Tuple, bid2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { ask3Tuple, ask2Tuple } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
    }
    @Test
    public void bbo()
        throws Exception
    {
        OrderBook book = new OrderBook(mSymbol);
        assertEquals(OrderBook.UNLIMITED_DEPTH,
                     book.getMaxDepth());
        doWrongSymbolTest(book);
        verifySnapshot(Arrays.asList(new QuantityTuple[0]),
                       Arrays.asList(new QuantityTuple[0]),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getBestBidAndOffer());
        // add a trade early on just to make sure the trade doesn't appear in any d-o-b messages
        book.processEvent(new TradeEvent(issueID(),
                                         System.currentTimeMillis(),
                                         mSymbol.getBaseSymbol(),
                                         "exchange", //$NON-NLS-1$
                                         new BigDecimal(100),
                                         new BigDecimal(50)));
        QuantityTuple tuple1 = new QuantityTuple(new BigDecimal("100"), //$NON-NLS-1$
                                                 new BigDecimal("0")); //$NON-NLS-1$
        // add a bid
        BidEvent bid = new BidEvent(issueID(),
                                    System.currentTimeMillis(),
                                    mSymbol.getBaseSymbol(),
                                    "exchange", //$NON-NLS-1$
                                    tuple1.getPrice(),
                                    tuple1.getSize());
        book.processEvent(bid);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { tuple1 } ),
                       Arrays.asList(new QuantityTuple[0]),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getBestBidAndOffer());
        // add an ask
        QuantityTuple tuple2 = new QuantityTuple(new BigDecimal("95"), //$NON-NLS-1$
                                                 new BigDecimal("10")); //$NON-NLS-1$
        AskEvent ask = new AskEvent(issueID(),
                                    System.currentTimeMillis(),
                                    mSymbol.getBaseSymbol(),
                                    "exchange", //$NON-NLS-1$
                                    tuple2.getPrice(),
                                    tuple2.getSize());
        book.processEvent(ask);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { tuple1 } ),
                       Arrays.asList(new QuantityTuple[] { tuple2 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getBestBidAndOffer());
        // add an ask, but not as good as the existing ("good" for offers means "lower price")
        QuantityTuple tuple3 = new QuantityTuple(new BigDecimal("105"), //$NON-NLS-1$
                                                 new BigDecimal("200")); //$NON-NLS-1$
        ask = new AskEvent(issueID(),
                           System.currentTimeMillis(),
                           mSymbol.getBaseSymbol(),
                           "exchange", //$NON-NLS-1$
                           tuple3.getPrice(),
                           tuple3.getSize());
        book.processEvent(ask);
        assertTrue(tuple3.getPrice().compareTo(tuple2.getPrice()) == 1);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { tuple1 } ),
                       Arrays.asList(new QuantityTuple[] { tuple2 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getBestBidAndOffer());
        // add a better ask
        QuantityTuple tuple4 = new QuantityTuple(new BigDecimal("75"), //$NON-NLS-1$
                                                 new BigDecimal("1")); //$NON-NLS-1$
        ask = new AskEvent(issueID(),
                           System.currentTimeMillis(),
                           mSymbol.getBaseSymbol(),
                           "exchange", //$NON-NLS-1$
                           tuple4.getPrice(),
                           tuple4.getSize());
        book.processEvent(ask);
        assertTrue(tuple4.getPrice().compareTo(tuple2.getPrice()) == -1);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { tuple1 } ),
                       Arrays.asList(new QuantityTuple[] { tuple4 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getBestBidAndOffer());
        // add a worse (lower) bid
        QuantityTuple tuple5 = new QuantityTuple(new BigDecimal("25"), //$NON-NLS-1$
                                                 new BigDecimal("250")); //$NON-NLS-1$
        bid = new BidEvent(issueID(),
                           System.currentTimeMillis(),
                           mSymbol.getBaseSymbol(),
                           "exchange", //$NON-NLS-1$
                           tuple5.getPrice(),
                           tuple5.getSize());
        book.processEvent(bid);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { tuple1 } ),
                       Arrays.asList(new QuantityTuple[] { tuple4 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getBestBidAndOffer());
        // add a better (higher) bid
        QuantityTuple tuple6 = new QuantityTuple(new BigDecimal("500"), //$NON-NLS-1$
                                                 new BigDecimal("12000")); //$NON-NLS-1$
        bid = new BidEvent(issueID(),
                           System.currentTimeMillis(),
                           mSymbol.getBaseSymbol(),
                           "exchange", //$NON-NLS-1$
                           tuple6.getPrice(),
                           tuple6.getSize());
        book.processEvent(bid);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { tuple6 } ),
                       Arrays.asList(new QuantityTuple[] { tuple4 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getBestBidAndOffer());
        assertNotNull(book.toString());
    }
    @Test
    public void latestTick()
        throws Exception
    {
        OrderBook book = new OrderBook(mSymbol);
        assertEquals(OrderBook.UNLIMITED_DEPTH,
                     book.getMaxDepth());
        doWrongSymbolTest(book);
        // latest tick from an empty book
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getLatestTick());
        // add ask
        QuantityTuple askQty1 = new QuantityTuple(new BigDecimal(100),
                                                  new BigDecimal(10));
        book.processEvent(new AskEvent(issueID(),
                                       System.currentTimeMillis(),
                                       mSymbol.getBaseSymbol(),
                                       "exchange", //$NON-NLS-1$
                                       askQty1.getPrice(),
                                       askQty1.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { askQty1 } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getLatestTick());
        // add bid
        QuantityTuple bidQty1 = new QuantityTuple(new BigDecimal(50),
                                                  new BigDecimal(20));
        book.processEvent(new BidEvent(issueID(),
                                       System.currentTimeMillis(),
                                       mSymbol.getBaseSymbol(),
                                       "exchange", //$NON-NLS-1$
                                       bidQty1.getPrice(),
                                       bidQty1.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty1 } ),
                       Arrays.asList(new QuantityTuple[] { askQty1 } ),
                       Arrays.asList(new QuantityTuple[] { } ), 
                       book.getLatestTick());
        // add trade
        QuantityTuple tradeQty1 = new QuantityTuple(new BigDecimal(750),
                                                    new BigDecimal(202));
        book.processEvent(new TradeEvent(issueID(),
                                         System.currentTimeMillis(),
                                         mSymbol.getBaseSymbol(),
                                         "exchange", //$NON-NLS-1$
                                         tradeQty1.getPrice(),
                                         tradeQty1.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty1 } ),
                       Arrays.asList(new QuantityTuple[] { askQty1 } ),
                       Arrays.asList(new QuantityTuple[] { tradeQty1 } ), 
                       book.getLatestTick());
        // replace ask
        QuantityTuple askQty2 = new QuantityTuple(new BigDecimal(5100),
                                                  new BigDecimal(1001));
        AskEvent ask2 = new AskEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     askQty2.getPrice(),
                                     askQty2.getSize());
        book.processEvent(ask2);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty1 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2 } ),
                       Arrays.asList(new QuantityTuple[] { tradeQty1 } ), 
                       book.getLatestTick());
        // replace bid
        QuantityTuple bidQty2 = new QuantityTuple(new BigDecimal(5010),
                                                  new BigDecimal(2090));
        BidEvent bid2 = new BidEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     bidQty2.getPrice(),
                                     bidQty2.getSize());
        book.processEvent(bid2);
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty2 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2 } ),
                       Arrays.asList(new QuantityTuple[] { tradeQty1 } ), 
                       book.getLatestTick());
        // replace trade
        QuantityTuple tradeQty2 = new QuantityTuple(new BigDecimal(1750),
                                                    new BigDecimal(1202));
        book.processEvent(new TradeEvent(issueID(),
                                         System.currentTimeMillis(),
                                         mSymbol.getBaseSymbol(),
                                         "exchange", //$NON-NLS-1$
                                         tradeQty2.getPrice(),
                                         tradeQty2.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty2 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2 } ),
                       Arrays.asList(new QuantityTuple[] { tradeQty2 } ), 
                       book.getLatestTick());
        // check that changes and deletes don't change the latest tick
        QuantityTuple notUsedQty1 = new QuantityTuple(new BigDecimal(1),
                                                      new BigDecimal(2));
        book.processEvent(convertEvent(bid2,
                                       notUsedQty1,
                                       Action.CHANGE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty2 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2 } ),
                       Arrays.asList(new QuantityTuple[] { tradeQty2 } ), 
                       book.getLatestTick());
        book.processEvent(convertEvent(ask2,
                                       Action.DELETE));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty2 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2 } ),
                       Arrays.asList(new QuantityTuple[] { tradeQty2 } ), 
                       book.getLatestTick());
        assertNotNull(book.toString());
    }
    @Test
    public void depthTests()
        throws Exception
    {
        OrderBook book = new OrderBook(mSymbol,
                                       2);
        assertEquals(2,
                     book.getMaxDepth());
        // each side can have a max of 2 orders
        // add each of the quantities in order making sure that the
        //  max depth is respected and that the oldest bid is dropped if necessary (not the lowest bid or highest ask)
        // the asks go from lowest to highest (descending value)
        QuantityTuple askQty1 = new QuantityTuple(new BigDecimal(10),
                                                  new BigDecimal(10));
        QuantityTuple askQty2 = new QuantityTuple(new BigDecimal(100),
                                                  new BigDecimal(10));
        QuantityTuple askQty3 = new QuantityTuple(new BigDecimal(1000),
                                                  new BigDecimal(10));
        // the bids go from highest to lowest (ascending value)
        QuantityTuple bidQty1 = new QuantityTuple(new BigDecimal(5000),
                                                  new BigDecimal(2090));
        QuantityTuple bidQty2 = new QuantityTuple(new BigDecimal(500),
                                                  new BigDecimal(2090));
        QuantityTuple bidQty3 = new QuantityTuple(new BigDecimal(50),
                                                  new BigDecimal(2090));
        // make sure the book is empty to start with - of course it is, why wouldn't it be?
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // add the events
        book.processEvent(new AskEvent(issueID(),
                                       System.currentTimeMillis(),
                                       mSymbol.getBaseSymbol(),
                                       "exchange", //$NON-NLS-1$
                                       askQty1.getPrice(),
                                       askQty1.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { askQty1 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        book.processEvent(new AskEvent(issueID(),
                                       System.currentTimeMillis(),
                                       mSymbol.getBaseSymbol(),
                                       "exchange", //$NON-NLS-1$
                                       askQty2.getPrice(),
                                       askQty2.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { askQty1, askQty2 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // the oldest ask should be dropped (ask1) - note that ask1 is the best ask, but it should still be removed
        book.processEvent(new AskEvent(issueID(),
                                       System.currentTimeMillis(),
                                       mSymbol.getBaseSymbol(),
                                       "exchange", //$NON-NLS-1$
                                       askQty3.getPrice(),
                                       askQty3.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { } ),
                       Arrays.asList(new QuantityTuple[] { askQty2, askQty3 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        book.processEvent(new BidEvent(issueID(),
                                       System.currentTimeMillis(),
                                       mSymbol.getBaseSymbol(),
                                       "exchange", //$NON-NLS-1$
                                       bidQty1.getPrice(),
                                       bidQty1.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty1 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2, askQty3 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        book.processEvent(new BidEvent(issueID(),
                                       System.currentTimeMillis(),
                                       mSymbol.getBaseSymbol(),
                                       "exchange", //$NON-NLS-1$
                                       bidQty2.getPrice(),
                                       bidQty2.getSize()));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty1, bidQty2 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2, askQty3 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        BidEvent bid3 = new BidEvent(issueID(),
                                     System.currentTimeMillis(),
                                     mSymbol.getBaseSymbol(),
                                     "exchange", //$NON-NLS-1$
                                     bidQty3.getPrice(),
                                     bidQty3.getSize());
        book.processEvent(bid3);
        // the oldest bid should be dropped (bid1) - note that bid1 is the best bid, but it should still be removed
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty2, bidQty3 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2, askQty3 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
        // test removing of an event from a book with a max depth
        book.processEvent(BidEvent.deleteEvent(bid3));
        verifySnapshot(Arrays.asList(new QuantityTuple[] { bidQty2 } ),
                       Arrays.asList(new QuantityTuple[] { askQty2, askQty3 } ),
                       Arrays.asList(new QuantityTuple[] { } ),
                       book.getDepthOfBook());
    }
    @Test
    public void invalidDepth()
        throws Exception
    {
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                new OrderBook(mSymbol,
                              0);
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                new OrderBook(mSymbol,
                              -2);
            }
        }.run();
        // this is OK because -1 happens to be the sentinel value
        assertNotNull(new OrderBook(mSymbol,
                                    -1));
    }
    /**
     * Verifies that the proper exception is thrown if an event for the wrong symbol is offered
     * to an order book.
     *
     * @param inOrderBook an <code>OrderBook</code> value
     * @throws Exception if an error occurs
     */
    private void doWrongSymbolTest(final OrderBook inOrderBook)
        throws Exception
    {
        // an event for the wrong symbol (checkEvent throws)
        // TODO add message from catalog
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                inOrderBook.processEvent(new BidEvent(issueID(),
                                                      System.currentTimeMillis(),
                                                      "COLIN", //$NON-NLS-1$
                                                      "exchange", //$NON-NLS-1$
                                                      new BigDecimal("1"), //$NON-NLS-1$
                                                      new BigDecimal("2"))); //$NON-NLS-1$
            }
        }.run();
    }
    /**
     * Verifies the contents of the given snapshot.
     *
     * @param inBids a <code>List&lt;QuantityTuple&gt;</code> value containing the values expected for the bid component of the snapshot or null
     * @param inAsks a <code>List&lt;QuantityTuple&gt;</code> value containing the values expected for the ask component of the snapshot or null
     * @param inTrades a <code>List&lt;QuantityTuple&gt;</code> value containing the values expected for the trade component of the snapshot or null
     * @param inSnapshot a <code>Message</code> value containing the value to be verified
     * @throws Exception if an error occurs
     */
    private void verifySnapshot(List<QuantityTuple> inBids,
                                List<QuantityTuple> inAsks,
                                List<QuantityTuple> inTrades, 
                                Message inSnapshot)
        throws Exception
    {
        // the snapshot may have bids, asks, trades, all, or none
        int bids = 0;
        int asks = 0;
        int trades = 0;
        Iterator<QuantityTuple> bidIterator = inBids.iterator();
        Iterator<QuantityTuple> askIterator = inAsks.iterator();
        Iterator<QuantityTuple> tradeIterator = inTrades.iterator();
        // this is the number of these components that exist in the snapshot
        int noEntries = inSnapshot.getInt(NoMDEntries.FIELD);
        // examine each one in turn
        for (int i = 1; i <= noEntries; i++) {
            // grab the group
            MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
            inSnapshot.getGroup(i,
                              group);
            // check the type
            char groupType = group.getChar(MDEntryType.FIELD);
            if(groupType == MDEntryType.BID) {
                assertFalse("There's supposed to be at least one bid in " + inSnapshot, //$NON-NLS-1$
                            inBids.isEmpty());
                bids += 1;
                verifyGroup(group,
                            bidIterator.next());
            } else if(groupType == MDEntryType.OFFER) {
                assertFalse("There's supposed to be at least one ask in " + inSnapshot, //$NON-NLS-1$
                            inAsks.isEmpty());
                asks += 1;
                verifyGroup(group,
                            askIterator.next());
            } else if(groupType == MDEntryType.TRADE) {
                assertFalse("There's supposed to be at least one trade in " + inSnapshot, //$NON-NLS-1$
                            inTrades.isEmpty());
                trades += 1;
                verifyGroup(group,
                            tradeIterator.next());
            } else {
                fail("Unknown group type: " + groupType); //$NON-NLS-1$
            }
        }
        // make sure that if we were expecting a group, we got one
        assertEquals(inBids.size(),
                     bids);
        assertEquals(inAsks.size(),
                     asks);
        assertEquals(inTrades.size(),
                     trades);
        // there should be no bids/asks left unexamined
        assertFalse(bidIterator.hasNext());
        assertFalse(askIterator.hasNext());
        assertFalse(tradeIterator.hasNext());
    }
    /**
     * Verifies the quantities of a single group. 
     *
     * @param inGroup a <code>MarketDataSnapshotFullRefresh.NoMDEntries</code> value containing the quantities to verify
     * @param inExpected a <code>QuantityTuple</code> value containing the expected quantities
     * @throws Exception if an error occurs
     */
    private void verifyGroup(MarketDataSnapshotFullRefresh.NoMDEntries inGroup,
                             QuantityTuple inExpected)
        throws Exception
    {
        assertEquals(inExpected,
                     new QuantityTuple(new BigDecimal(inGroup.getString(MDEntryPx.FIELD).toString()),
                                       new BigDecimal(inGroup.getString(MDEntrySize.FIELD).toString())));
    }
    /**
     * ID counter
     */
    private long mCounter = 0;
    /**
     * Returns a unique ID.
     *
     * @return a <code>long</code> value
     */
    private long issueID()
    {
        return ++mCounter;
    }
    /**
     * Creates an event that exactly matches the given event except for the given action.
     * 
     * <p>Use this method to generate, for example, a request to delete an event already added to an {@link OrderBook}.
     *
     * @param inEvent a <code>BidAskEvent</code> value
     * @param inAction an <code>Action</code> value
     * @return a <code>BidAskEvent</code> value that differs from the given event only by the given <code>Action</code>
     * @throws UnsupportedOperationException if conversion of the given event is not supported
     */
    public static BidAskEvent convertEvent(BidAskEvent inEvent,
                                           Action inAction)
    {
        return convertEvent(inEvent,
                            new QuantityTuple(inEvent.getPrice(),
                                              inEvent.getSize()),
                                              inAction);
    }
    /**
     * Creates an event that exactly matches the given event except for the given action and quantities.
     * 
     * <p>Use this method to generate, for example, a request to change an event already added to an {@link OrderBook}.
     *
     * @param inEvent a <code>BidAskEvent</code> value
     * @param inTuple a <code>QuantityTuple</code> value
     * @param inAction an <code>Action</code> value
     * @return a <code>BidAskEvent</code> value that differs from the given event only by the given <code>Action</code> and quantities
     * @throws UnsupportedOperationException if conversion of the given event is not supported
     */
    public static BidAskEvent convertEvent(BidAskEvent inEvent,
                                           QuantityTuple inTuple,
                                           Action inAction)
    {
        if(inEvent instanceof BidEvent) {
            return new BidEvent(inEvent.getMessageId(),
                                inEvent.getTimestamp(),
                                inEvent.getSymbol(),
                                inEvent.getExchange(),
                                inTuple.getPrice(),
                                inTuple.getSize(),
                                inAction);
        }
        if(inEvent instanceof AskEvent) {
            return new AskEvent(inEvent.getMessageId(),
                                inEvent.getTimestamp(),
                                inEvent.getSymbol(),
                                inEvent.getExchange(),
                                inTuple.getPrice(),
                                inTuple.getSize(),
                                inAction);
        }
        throw new UnsupportedOperationException();
    }
}
