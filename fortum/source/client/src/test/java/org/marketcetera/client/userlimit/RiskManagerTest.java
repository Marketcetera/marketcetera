package org.marketcetera.client.userlimit;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.client.*;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.Pair;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.*;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.*;
import org.marketcetera.trade.*;

import quickfix.field.OrdType;
import quickfix.field.Price;
import quickfix.field.Quantity;

/* $License$ */

/**
 * Tests {@link RiskManager}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RiskManagerTest
{
    /**
     * One-time setup necessary for FIX.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        msgFactory = fixVersion.getMessageFactory();
        fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        if(fixDD == null) {
            FIXDataDictionaryManager.initialize(fixVersion,
                                                fixVersion.getDataDictionaryURL());
            fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        }
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(fixDD);
    }
    /**
     * Executes setup before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        RiskManager.INSTANCE.client = testClient;
        SymbolDataCollection.setClient(testClient);
        userData.clear();
        positionData.clear();
        feed = new MockMarketDataFeed();
        feed.clearDataCache();
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
   }
    /**
     * Tests bad values to {@link RiskManager#inspect(org.marketcetera.trade.Order)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBadValues()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                RiskManager.INSTANCE.inspect(null);
            }
        };
    }
    /**
     * Tests inspection of {@link OrderCancel} values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOrderCancel()
            throws Exception
    {
        OrderCancel cancel = ClientTest.createOrderCancel();
        RiskManager.INSTANCE.inspect(cancel);
    }
    /**
     * Tests limit orders of less than one penny.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testLessThanOnePenny()
            throws Exception
    {
        // setup data for test - create a list of orders with the given price/qty
        OrderList orders = OrderList.generate(new BigDecimal("0.001"),
                                              new BigDecimal("1"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(1000),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("0.01"),
                        new BigDecimal("0.02"),
                        new BigDecimal("0.03"));
        // make sure that all the order types for all the instrument types fail for the same reason
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            if(orderPair.getFirstMember().getType() == OrderType.Market) {
                RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
            } else {
                new ExpectedFailure<UserLimitViolation>(Messages.LESS_THAN_A_PENNY) {
                    @Override
                    protected void run()
                            throws Exception
                    {
                        RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                    }
                };
            }
        }
    }
    /**
     * Tests limit orders of exactly one penny.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testExactlyOnePenny()
            throws Exception
    {
        // setup data for test - create a list of orders with the given price/qty
        OrderList orders = OrderList.generate(new BigDecimal("0.01"),
                                              new BigDecimal("1"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(1000),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("0.01"),
                        new BigDecimal("0.02"),
                        new BigDecimal("0.03"));
        // make sure that all the order types for all the instrument types fail for the same reason
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests limit orders of more than one penny.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMoreThanOnePenny()
            throws Exception
    {
        // setup data for test - create a list of orders with the given price/qty
        OrderList orders = OrderList.generate(new BigDecimal("0.02"),
                                              new BigDecimal("1"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(1000),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("0.01"),
                        new BigDecimal("0.02"),
                        new BigDecimal("0.03"));
        // make sure that all the order types for all the instrument types fail for the same reason
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests the absence of symbol data.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNoSymbolData()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("0.02"),
                                              new BigDecimal("1"));
        setupMarketdata(new BigDecimal("0.01"),
                        new BigDecimal("0.02"),
                        new BigDecimal("0.03"));
        // make sure that all the order types for all the instrument types fail for the same reason
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            new ExpectedFailure<UserLimitViolation>(Messages.NO_SYMBOL_DATA) {
                @Override
                protected void run()
                        throws Exception
                {
                    RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                }
            };
        }
    }
    /**
     * Tests the absence of trade data.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNoTradeData()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("0.02"),
                                              new BigDecimal("1"));
        setupMarketdata(null,
                        new BigDecimal("0.02"),
                        new BigDecimal("0.03"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(56),
                  new BigDecimal(100),
                  new BigDecimal(100));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            new ExpectedFailure<UserLimitViolation>(Messages.NO_TRADE_DATA) {
                @Override
                protected void run()
                        throws Exception
                {
                    RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                }
            };
        }
    }
    /**
     * Tests the absence of bid data.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNoBidData()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("0.02"),
                                              new BigDecimal("1"));
        setupMarketdata(new BigDecimal("0.02"),
                        new BigDecimal("0.02"),
                        null);
        setLimits(new BigDecimal(1000),
                  new BigDecimal(56),
                  new BigDecimal(100),
                  new BigDecimal(100));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            new ExpectedFailure<UserLimitViolation>(Messages.NO_QUOTE_DATA) {
                @Override
                protected void run()
                        throws Exception
                {
                    RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                }
            };
        }
    }
    /**
     * Tests the absence of ask data.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNoAskData()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("0.02"),
                                              new BigDecimal("1"));
        setupMarketdata(new BigDecimal("0.02"),
                        null,
                        new BigDecimal("0.02"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(56),
                  new BigDecimal(100),
                  new BigDecimal(100));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            new ExpectedFailure<UserLimitViolation>(Messages.NO_QUOTE_DATA) {
                @Override
                protected void run()
                        throws Exception
                {
                    RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                }
            };
        }
    }
    /**
     * Tests orders with a position less than max position.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testPositionLessThanMaxPosition()
            throws Exception
    {
        // setup data for test - create a list of orders with the given price/qty
        OrderList orders = OrderList.generate(new BigDecimal("1"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(56),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("11"));
        // make sure that all the order types for all the instrument types fail for the same reason
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests orders with a position value less than max position.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testLessThanMaxPosition()
            throws Exception
    {
        // setup data for test - create a list of orders with the given price/qty
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(56),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("11"));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests orders with a position equal to max position.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEqualMaxPosition()
            throws Exception
    {
        // setup data for test - create a list of orders with the given price/qty
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(50),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("10"),
                        new BigDecimal("10"),
                        new BigDecimal("10"));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests orders with greater than max position.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGreaterMaxPosition()
            throws Exception
    {
        // setup data for test - create a list of orders with the given price/qty
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(1),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("11"));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            new ExpectedFailure<UserLimitViolation>(Messages.POSITION_LIMIT_EXCEEDED) {
                @Override
                protected void run()
                        throws Exception
                {
                    RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                }
            };
        }
    }
    /**
     * Tests orders with less than the minimum allowed trade value.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testLessThanMaxValue()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(1000),
                  new BigDecimal(100),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("10"));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests orders with equal to the minimum allowed trade value.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEqualToMaxValue()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(50),
                  new BigDecimal(100),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("10"));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests orders with greater than the minimum allowed trade value.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGreaterThanMaxValue()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(45),
                  new BigDecimal(100),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("10"));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            new ExpectedFailure<UserLimitViolation>(Messages.VALUE_LIMIT_EXCEEDED) {
                @Override
                protected void run()
                        throws Exception
                {
                    RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                }
            };
        }
    }
    /**
     * Tests orders with respect to maximum position when position starts negative.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNegativePosition()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(100),
                  new BigDecimal(12),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("10"));
        setStartingPosition(new BigDecimal(-10));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            // if side is sell or sell short, this test should fail
            if(orderPair.getFirstMember().getSide() == Side.Buy)  {
                RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
            } else {
                new ExpectedFailure<UserLimitViolation>(Messages.POSITION_LIMIT_EXCEEDED) {
                    @Override
                    protected void run()
                            throws Exception
                    {
                        RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                    }
                };
            }
        }
    }
    /**
     * Tests orders with respect to maximum position when position starts at zero. 
     *
     * <p>Note this is subtly different than starting with no position.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testZeroPosition()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(100),
                  new BigDecimal(12),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("10"));
        setStartingPosition(new BigDecimal(0));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
        }
    }
    /**
     * Tests orders with respect to maximum position when position starts greater than zero.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testPositivePosition()
            throws Exception
    {
        OrderList orders = OrderList.generate(new BigDecimal("10"),
                                              new BigDecimal("5"));
        setLimits(new BigDecimal(100),
                  new BigDecimal(12),
                  new BigDecimal(100),
                  new BigDecimal(100));
        setupMarketdata(new BigDecimal("9"),
                        new BigDecimal("10"),
                        new BigDecimal("10"));
        setStartingPosition(new BigDecimal(10));
        for(final Pair<OrderAttributes,Order> orderPair : orders) {
            // if side is sell or sell short, this test should pass
            if(orderPair.getFirstMember().getSide() != Side.Buy)  {
                RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
            } else {
                new ExpectedFailure<UserLimitViolation>(Messages.POSITION_LIMIT_EXCEEDED) {
                    @Override
                    protected void run()
                            throws Exception
                    {
                        RiskManager.INSTANCE.inspect(orderPair.getSecondMember());
                    }
                };
            }
        }
    }
    /**
     * Sets the starting position for all the test instruments to the given value.
     *
     * @param inPosition a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void setStartingPosition(BigDecimal inPosition)
            throws Exception
    {
        for(Instrument instrument : instruments) {
            positionData.put(instrument,
                             inPosition);
        }
    }
    /**
     * Sets the symbol data limits as specified. 
     *
     * @param inMaximumTradeValue
     * @param inMaximumPosition
     * @param inMaximumDeviationFromLast
     * @param inMaximumDeviationFromMid
     * @throws Exception
     */
    private void setLimits(BigDecimal inMaximumTradeValue,
                           BigDecimal inMaximumPosition,
                           BigDecimal inMaximumDeviationFromLast,
                           BigDecimal inMaximumDeviationFromMid)
            throws Exception
    {
        for(Instrument instrument : instruments) {
            SymbolDataCollection symbolData = new SymbolDataCollection();
            symbolData.add(new SymbolData(instrument.getSymbol(),
                                          inMaximumPosition,
                                          inMaximumTradeValue,
                                          inMaximumDeviationFromLast,
                                          inMaximumDeviationFromMid));
            symbolData.store();
        }
    }
    private void setupMarketdata(BigDecimal inTradePrice,
                                 BigDecimal inAskPrice,
                                 BigDecimal inBidPrice)
            throws Exception
    {
        feed.clearDataCache();
        // setup market data for equity
        MarketDataFeedTokenSpec equitySpec = MarketDataFeedTokenSpec.generateTokenSpec(MarketDataRequestBuilder.newRequestFromString("symbols=" + equity.getSymbol()),
                                                                                       new ISubscriber[0]);
        MarketDataFeedTokenSpec optionSpec = MarketDataFeedTokenSpec.generateTokenSpec(MarketDataRequestBuilder.newRequestFromString("assetClass=OPTION:symbols=" + option.getSymbol()),
                                                                                       new ISubscriber[0]);
        MarketDataFeedTokenSpec futureSpec = MarketDataFeedTokenSpec.generateTokenSpec(MarketDataRequestBuilder.newRequestFromString("assetClass=FUTURE:symbols=" + future.getSymbol()),
                                                                                       new ISubscriber[0]);
        equityToken = feed.execute(equitySpec);
        optionToken = feed.execute(optionSpec);
        futureToken = feed.execute(futureSpec);
        if(inTradePrice != null) {
            TradeEvent tradeEvent = EventTestBase.generateEquityTradeEvent(equity,
                                                                           inTradePrice);
            feed.submitData(equityToken.getHandle(),
                            tradeEvent);
            tradeEvent = EventTestBase.generateOptionTradeEvent(option,
                                                                equity,
                                                                inTradePrice);
            feed.submitData(optionToken.getHandle(),
                            tradeEvent);
            tradeEvent = EventTestBase.generateFutureTradeEvent(future,
                                                                inTradePrice);
            feed.submitData(futureToken.getHandle(),
                            tradeEvent);
        }
        if(inBidPrice != null) {
            BidEvent bidEvent = EventTestBase.generateEquityBidEvent(equity,
                                                                     inBidPrice);
            feed.submitData(equityToken.getHandle(),
                            bidEvent);
            bidEvent = EventTestBase.generateOptionBidEvent(option,
                                                            equity,
                                                            inBidPrice);
            feed.submitData(optionToken.getHandle(),
                            bidEvent);
            bidEvent = EventTestBase.generateFutureBidEvent(future,
                                                            inBidPrice);
            feed.submitData(futureToken.getHandle(),
                            bidEvent);
        }
        if(inAskPrice != null) {
            AskEvent askEvent = EventTestBase.generateEquityAskEvent(equity,
                                                                     inAskPrice);
            feed.submitData(equityToken.getHandle(),
                            askEvent);
            askEvent = EventTestBase.generateOptionAskEvent(option,
                                                            equity,
                                                            inAskPrice);
            feed.submitData(optionToken.getHandle(),
                            askEvent);
            askEvent = EventTestBase.generateFutureAskEvent(future,
                                                            inAskPrice);
            feed.submitData(futureToken.getHandle(),
                            askEvent);
        }
        equityToken.cancel();
        optionToken.cancel();
        futureToken.cancel();
    }
    private static Properties userData = new Properties();
    /**
     * test positions to for the client to return
     */
    private static Map<Instrument,BigDecimal> positionData = new HashMap<Instrument,BigDecimal>();
    /**
     * test client used to avoid needing a connection to a running server
     */
    private static final Client testClient = new Client() {
        @Override
        public void addBrokerStatusListener(BrokerStatusListener inListener)
        {
        }
        @Override
        public void addExceptionListener(ExceptionListener inListener)
        {
        }
        @Override
        public void addReportListener(ReportListener inListener)
        {
        }
        @Override
        public void addServerStatusListener(ServerStatusListener inListener)
        {
        }
        @Override
        public void close()
        {
        }
        @Override
        public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(Date inDate)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(Date inDate)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(Date inDate)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public BrokersStatus getBrokersStatus()
                throws ConnectionException
        {
            return null;
        }
        @Override
        public BigDecimal getEquityPositionAsOf(Date inDate,
                                                Equity inEquity)
                throws ConnectionException
        {
            return positionData.get(inEquity);
        }
        @Override
        public BigDecimal getFuturePositionAsOf(Date inDate,
                                                Future inFuture)
                throws ConnectionException
        {
            return positionData.get(inFuture);
        }
        @Override
        public Date getLastConnectTime()
        {
            return null;
        }
        @Override
        public BigDecimal getOptionPositionAsOf(Date inDate,
                                                Option inOption)
                throws ConnectionException
        {
            return positionData.get(inOption);
        }
        @Override
        public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                           String... inRootSymbols)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public Collection<String> getOptionRoots(String inUnderlying)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public ClientParameters getParameters()
        {
            return null;
        }
        @Override
        public ReportBase[] getReportsSince(Date inDate)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public String getUnderlying(String inOptionRoot)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public Properties getUserData()
                throws ConnectionException
        {
            return userData;
        }
        @Override
        public UserInfo getUserInfo(UserID inId,
                                    boolean inUseCache)
                throws ConnectionException
        {
            return null;
        }
        @Override
        public boolean isCredentialsMatch(String inUsername,
                                          char[] inPassword)
        {
            return false;
        }
        @Override
        public boolean isServerAlive()
        {
            return false;
        }
        @Override
        public void reconnect()
                throws ConnectionException
        {
        }
        @Override
        public void reconnect(ClientParameters inParameters)
                throws ConnectionException
        {
        }
        @Override
        public void removeBrokerStatusListener(BrokerStatusListener inListener)
        {
        }
        @Override
        public void removeExceptionListener(ExceptionListener inListener)
        {
        }
        @Override
        public void removeReportListener(ReportListener inListener)
        {
        }
        @Override
        public void removeServerStatusListener(ServerStatusListener inListener)
        {
        }
        @Override
        public void sendOrder(OrderSingle inOrderSingle)
                throws ConnectionException, OrderValidationException
        {
        }
        @Override
        public void sendOrder(OrderReplace inOrderReplace)
                throws ConnectionException, OrderValidationException
        {
        }
        @Override
        public void sendOrder(OrderCancel inOrderCancel)
                throws ConnectionException, OrderValidationException
        {
        }
        @Override
        public void sendOrderRaw(FIXOrder inFIXOrder)
                throws ConnectionException, OrderValidationException
        {
        }
        @Override
        public void setUserData(Properties inProperties)
                throws ConnectionException
        {
            userData = inProperties;
        }
    };
    private MockMarketDataFeed feed;
    private MockMarketDataFeedToken equityToken;
    private MockMarketDataFeedToken optionToken;
    private MockMarketDataFeedToken futureToken;
    private static FIXMessageFactory msgFactory;
    private static FIXVersion fixVersion = FIXVersion.FIX42;
    private static FIXDataDictionary fixDD;
    private final static Equity equity = new Equity("GOOG");
    private final static Option option = new Option("AAPL",
                                                    "20150319",
                                                    BigDecimal.ONE,
                                                    OptionType.Call);
    private final static Future future = new Future("ENOYR-11");
    private final static Instrument[] instruments = new Instrument[] { equity, option, future };
    public static class OrderAttributes
    {
        /**
         * Get the type value.
         *
         * @return a <code>OrderType</code> value
         */
        public OrderType getType()
        {
            return type;
        }
        /**
         * Get the side value.
         *
         * @return a <code>Side</code> value
         */
        public Side getSide()
        {
            return side;
        }
        private OrderAttributes(OrderType inType,
                                Side inSide)
        {
            type = inType;
            side = inSide;
        }
        private final OrderType type;
        private final Side side;
    }
    public static class OrderList
            implements Iterable<Pair<OrderAttributes,Order>>
    {
        private final List<Pair<OrderAttributes,Order>> generatedOrders = new ArrayList<Pair<OrderAttributes,Order>>();
        public static OrderList generate(BigDecimal inPrice,
                                         BigDecimal inQuantity)
                throws Exception
        {
            OrderList orderSet = new OrderList();
            BigDecimal[] prices = new BigDecimal[] { null, inPrice };
            for(Instrument instrument : instruments) {
                for(Side side : EnumSet.of(Side.Buy, Side.Sell, Side.SellShort)) {
                    for(BigDecimal price : prices) {
                        OrderSingle single = ClientTest.createOrderSingle();
                        single.setInstrument(instrument);
                        single.setQuantity(inQuantity);
                        single.setSide(side);
                        if(price == null) {
                            single.setOrderType(OrderType.Market);
                            single.setPrice(null);
                        } else {
                            single.setOrderType(OrderType.Limit);
                            single.setPrice(price);
                        }
                        orderSet.generatedOrders.add(new Pair<OrderAttributes,Order>(new OrderAttributes(single.getOrderType(),
                                                                                                         single.getSide()),
                                                                                     single));
                        OrderReplace replace = ClientTest.createOrderReplace();
                        replace.setInstrument(instrument);
                        replace.setQuantity(inQuantity);
                        replace.setSide(side);
                        if(price == null) {
                            replace.setOrderType(OrderType.Market);
                            replace.setPrice(null);
                        } else {
                            replace.setOrderType(OrderType.Limit);
                            replace.setPrice(price);
                        }
                        orderSet.generatedOrders.add(new Pair<OrderAttributes,Order>(new OrderAttributes(replace.getOrderType(),
                                                                                                         replace.getSide()),
                                                                                     replace));
                        FIXOrder order = Factory.getInstance().createOrder(FIXConverter.toQMessage(msgFactory,
                                                                                                   fixDD.getDictionary(),
                                                                                                   single),
                                                                           new BrokerID("broker"));
                        quickfix.field.Symbol symbolField = new quickfix.field.Symbol(instrument.getSymbol());
                        order.getMessage().setField(symbolField);
                        OrderType type;
                        if(price == null) {
                            order.getMessage().setField(new quickfix.field.OrdType(OrdType.MARKET));
                            order.getMessage().removeField(quickfix.field.Price.FIELD);
                            type = OrderType.Market;
                        } else {
                            order.getMessage().setField(new quickfix.field.OrdType(OrdType.LIMIT));
                            order.getMessage().setField(new Price(price));
                            type = OrderType.Limit;
                        }
                        switch(side) {
                            case Buy:
                                order.getMessage().setField(new quickfix.field.Side(quickfix.field.Side.BUY));
                                break;
                            case Sell:
                                order.getMessage().setField(new quickfix.field.Side(quickfix.field.Side.SELL));
                                break;
                            case SellShort:
                                order.getMessage().setField(new quickfix.field.Side(quickfix.field.Side.SELL_SHORT));
                                break;
                            default:
                                throw new UnsupportedOperationException();
                        }
                        order.getMessage().setField(new Quantity(inQuantity));
                        order.getMessage().setField(new quickfix.field.OrderID(String.valueOf(System.nanoTime())));
                        orderSet.generatedOrders.add(new Pair<OrderAttributes,Order>(new OrderAttributes(type,
                                                                                                         side),
                                                                                     order));
                    }
                }
            }
            return orderSet;
        }
        /* (non-Javadoc)
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<Pair<OrderAttributes,Order>> iterator()
        {
            return generatedOrders.iterator();
        }
        private OrderList()
        {
        }
    }
}
