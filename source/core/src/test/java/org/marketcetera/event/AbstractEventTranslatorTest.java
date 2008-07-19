package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Arrays;

import junit.framework.TestSuite;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.marketdata.MockMarketDataFeedCredentials;
import org.marketcetera.marketdata.*;

import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

/* $License$ */
/**
 * Tests {@link AbstractEventTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class AbstractEventTranslatorTest
        extends MarketDataFeedTestBase
{
    private MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> mSpec;
    private MockMarketDataFeed mFeed;
    private String mSymbol;
    private MockEventTranslator mTranslator;
    /**
     * Create a new AbstractEventTranslatorTest instance.
     *
     * @param inName
     */
    public AbstractEventTranslatorTest(String inName)
    {
        super(inName);
    }
    protected static TestSuite suite() 
    {
        return MarketDataFeedTestBase.suite(AbstractEventTranslatorTest.class);
    }        
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        mSymbol = "YHOO";
        mSpec = MarketDataFeedTokenSpec.generateTokenSpec(mCredentials, 
                                                          AbstractMarketDataFeed.levelOneMarketDataRequest(Arrays.asList(new MSymbol[] { new MSymbol(mSymbol) }), 
                                                                                                           true), 
                                                          Arrays.asList(new MockSubscriber[] { new MockSubscriber() } ));
        mFeed = new MockMarketDataFeed();
        mFeed.start();
        mTranslator = new MockEventTranslator();
    }
    /**
     * Test control paths through {@link AbstractEventTranslator#updateEventFixMessageSnapshot(EventBase)}.
     *
     * @throws Exception
     */
    public void testUpdateFIXMessage()
        throws Exception
    {
        // submit a query that will give us a handle to work with
        MockMarketDataFeedToken token = mFeed.execute(mSpec);
        // verify the snapshot is empty to start with
        verifySnapshot(mSymbol,
                       token,
                       null,
                       null,
                       null);
        // test a null
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                mTranslator.updateEventFixMessageSnapshot(null);
            }
        }.run();
        // should still be empty
        verifySnapshot(mSymbol,
                       token,
                       null,
                       null,
                       null);
        // test an EventBase subclass that is not a SymbolExchangeEvent
        mTranslator.updateEventFixMessageSnapshot(new UnknownEvent(System.currentTimeMillis(),
                                                      System.currentTimeMillis()));
        // still empty
        verifySnapshot(mSymbol,
                       token,
                       null,
                       null,
                       null);
        // now, submit a bid to the feed that will update the snapshot
        QuantityTuple bid = new QuantityTuple(new BigDecimal("123.456789"),
                                              new BigDecimal("2345.678"));
        BidEvent bidE = new BidEvent(System.currentTimeMillis(),
                                     System.currentTimeMillis(),
                                     null,
                                     mSymbol,
                                     "my-exchange",
                                     bid.getPrice(),
                                     bid.getSize());
        mFeed.submitData(token.getHandle(), 
                         bidE);
        verifySnapshot(mSymbol,
                       token,
                       bid,
                       null,
                       null);
        // add an ask
        QuantityTuple ask = new QuantityTuple(new BigDecimal("10987654.321"),
                                              new BigDecimal("23571113.171923"));
        AskEvent askE = new AskEvent(System.currentTimeMillis(),
                                     System.currentTimeMillis(),
                                     null,
                                     mSymbol,
                                     "my-exchange",
                                     ask.getPrice(),
                                     ask.getSize());
        mFeed.submitData(token.getHandle(), 
                         askE);
        verifySnapshot(mSymbol,
                       token,
                       bid,
                       ask,
                       null);
        // add a trade
        QuantityTuple trade = new QuantityTuple(new BigDecimal("1020304050.607080"),
                                                new BigDecimal("100908070.605040"));
        TradeEvent tradeE = new TradeEvent(System.currentTimeMillis(),
                                           System.currentTimeMillis(),
                                           mSymbol,
                                           "my-exchange",
                                           trade.getPrice(),
                                           trade.getSize(),
                                           null);
        mFeed.submitData(token.getHandle(), 
                         tradeE);
        verifySnapshot(mSymbol,
                       token,
                       bid,
                       ask,
                       trade);
        // replace the ask with a new ask
        ask = new QuantityTuple(new BigDecimal("100200300400.500600"),
                                new BigDecimal("12345678.91011121314"));
        askE = new AskEvent(System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            null,
                            mSymbol,
                            "my-exchange",
                            ask.getPrice(),
                            ask.getSize());
        mFeed.submitData(token.getHandle(), 
                         askE);
        verifySnapshot(mSymbol,
                       token,
                       bid,
                       ask,
                       trade);
        // replace the bid with a new bid
        bid = new QuantityTuple(new BigDecimal("7777.88888"),
                                new BigDecimal("3333.4444444"));
        bidE = new BidEvent(System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            null,
                            mSymbol,
                            "my-exchange",
                            bid.getPrice(),
                            bid.getSize());
        mFeed.submitData(token.getHandle(), 
                         bidE);
        verifySnapshot(mSymbol,
                       token,
                       bid,
                       ask,
                       trade);
        // replace the trade with a new trade
        trade = new QuantityTuple(new BigDecimal("5101520.253035340"),
                                  new BigDecimal("90807060.50403020"));
        tradeE = new TradeEvent(System.currentTimeMillis(),
                                System.currentTimeMillis(),
                                mSymbol,
                                "my-exchange",
                                trade.getPrice(),
                                trade.getSize(),
                                null);
        mFeed.submitData(token.getHandle(), 
                         tradeE);
        verifySnapshot(mSymbol,
                       token,
                       bid,
                       ask,
                       trade);
        // submit an ask for a different symbol, make sure it doesn't affect this one
        String newSymbol = "colin-is-the-symbol";
        MockSubscriber t2 = new MockSubscriber();
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> newSpec = MarketDataFeedTokenSpec.generateTokenSpec(mSpec.getCredentials(),
                                                                                                                   AbstractMarketDataFeed.levelOneMarketDataRequest(Arrays.asList(new MSymbol[] { new MSymbol(newSymbol) } ), 
                                                                                                                                                                    true),
                                                                                                                   Arrays.asList(new MockSubscriber[] { t2 } ));
        MockMarketDataFeedToken newToken = mFeed.execute(newSpec);
        assertFalse(newToken.getHandle().equals(token.getHandle()));
        waitForPublication(t2);
        QuantityTuple newAsk = new QuantityTuple(new BigDecimal("7654321.01234567"),
                                                 new BigDecimal("1123485923.1273495"));
        askE = new AskEvent(System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            null,
                            newSymbol,
                            "my-exchange",
                            newAsk.getPrice(),
                            newAsk.getSize());
        mFeed.submitData(newToken.getHandle(), 
                         askE);
        // verify old snapshot hasn't changed
        verifySnapshot(mSymbol,
                       token,
                       bid,
                       ask,
                       trade);
        // verify new snapshot
        verifySnapshot(newSymbol,
                       newToken,
                       null,
                       newAsk,
                       null);
    }
    /**
     * Verifies the contents of the snapshot embedded in the publications to the subscribers.
     *
     * @param inSymbol a <code>String</code> value
     * @param inToken a <code>TestMarketDataFeedToken</code> value
     * @param inBid a <code>QuantityTuple</code> value containing the value expected for the bid component of the snapshot or null
     * @param inAsk a <code>QuantityTuple</code> value containing the value expected for the ask component of the snapshot or null
     * @param inTrade a <code>QuantityTuple</code> value containing the value expected for the trade component of the snapshot or null
     * @throws Exception if an error occurs
     */
    private void verifySnapshot(String inSymbol,
                                MockMarketDataFeedToken inToken,
                                QuantityTuple inBid,
                                QuantityTuple inAsk,
                                QuantityTuple inTrade)
        throws Exception
    {
        // to verify the snapshot held in the belly of the translator, pass in an EventBase object
        //  into the event translator loop and verify the FIX message that comes out
        // grab a subscriber that we can guarantee will be notified by the new data
        final MockSubscriber subscriber = (MockSubscriber)inToken.getTokenSpec().getSubscribers().get(0);
        subscriber.reset();
        // create an event that won't change the information in the snapshot returned but will trigger
        //  the update mechanism
        DoNothingEvent event = new DoNothingEvent(inSymbol);
        // force the feed to report our event directly as data received from the data feed
        mFeed.submitData(inToken.getHandle(), 
                         event);
        // wait for the subscriber to be notified
        waitForPublication(subscriber);
        // grab the (updated) FIX message from the subscriber and verify the contents
        Message snapshot = ((SymbolExchangeEvent)subscriber.getData()).getFIXMessage();
        // the snapshot may have a bid, ask, or trade, or some combination of the three
        boolean hasBid = false;
        boolean hasAsk = false;
        boolean hasTrade = false;
        // this is the number of these components that exist in the snapshot
        int noEntries = snapshot.getInt(NoMDEntries.FIELD);
        // examine each one in turn
        for (int i = 1; i <= noEntries; i++){
            // grab the group
            MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
            snapshot.getGroup(i, 
                              group);
            // check the type
            char groupType = group.getChar(MDEntryType.FIELD);
            if(groupType == MDEntryType.BID) {
                assertFalse(inBid == null);
                hasBid = true;
                verifyGroup(group,
                            inBid);
            } else if(groupType == MDEntryType.OFFER) {
                assertFalse(inAsk == null);
                hasAsk = true;
                verifyGroup(group,
                            inAsk);
            } else if(groupType == MDEntryType.TRADE) {
                assertFalse(inTrade == null);
                hasTrade = true;
                verifyGroup(group,
                            inTrade);
            } else {
                fail("Unknown group type: " + groupType);
            }
        }
        // make sure that if we were expecting a group, we got one
        assertEquals(inBid != null,
                     hasBid);
        assertEquals(inAsk != null,
                     hasAsk);
        assertEquals(inTrade != null,
                     hasTrade);
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
     * An <code>EventBase</code> subclass that has symbol and exchange info but will not cause the
     * snapshot record for the symbol to be updated.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.5.0
     */
    public static class DoNothingEvent
        extends SymbolExchangeEvent
    {
        /**
         * Create a new DoNothingEvent instance.
         *
         * @param inSymbol a <code>String</code> value
         */
        public DoNothingEvent(String inSymbol)
        {
            super(System.currentTimeMillis(),
                  System.currentTimeMillis(),
                  null,
                  inSymbol,
                  "My-exchange");
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("DoNothingEvent %d",
                                 hashCode());
        }
    }
    /**
     * A wrapper class for the quantities associated with a symbol event.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.5.0
     */
    private static class QuantityTuple
    {
        /**
         * the price associated with the event
         */
        private final BigDecimal mPrice;
        /**
         * the size associated with the event
         */
        private final BigDecimal mSize;
        /**
         * Create a new QuantityTuple instance.
         *
         * @param inPrice a <code>BigDecimal</code> value
         * @param inSize a <code>BigDecimal</code> value
         */
        private QuantityTuple(BigDecimal inPrice,
                              BigDecimal inSize)
        {
            mPrice = inPrice;
            mSize = inSize;
        }
        /**
         * Gets the price associated with the event.
         *
         * @return a <code>BigDecimal</code> value
         */
        private BigDecimal getPrice()
        {
            return mPrice;
        }
        /**
         * Gets the size associated with the event.
         *
         * @return a <code>BigDecimal</code> value
         */
        private BigDecimal getSize()
        {
            return mSize;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mPrice == null) ? 0 : mPrice.hashCode());
            result = prime * result + ((mSize == null) ? 0 : mSize.hashCode());
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final QuantityTuple other = (QuantityTuple) obj;
            if (mPrice == null) {
                if (other.mPrice != null)
                    return false;
            } else if (!mPrice.equals(other.mPrice))
                return false;
            if (mSize == null) {
                if (other.mSize != null)
                    return false;
            } else if (!mSize.equals(other.mSize))
                return false;
            return true;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return new StringBuilder().append(getSize()).append(" ").append(getPrice()).toString();
        }
    }
}
