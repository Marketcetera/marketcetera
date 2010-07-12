package org.marketcetera.client.userlimit;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.client.*;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.AbstractMarketDataFeed.Data;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.*;

import quickfix.field.Price;
import quickfix.field.Quantity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RiskManagerTest
{
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
        RiskManager.INSTANCE.provider = marketDataProvider;
        SymbolDataCollection.setClient(testClient);
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
    @Test@Ignore
    public void testSmallOrder()
            throws Exception
    {
        final OrderSingle single = ClientTest.createOrderSingle();
        establishData(single.getInstrument().getSymbol());
        single.setPrice(new BigDecimal("0.001"));
        new ExpectedFailure<UserLimitViolation>(Messages.LESS_THAN_A_PENNY){
            @Override
            protected void run()
                    throws Exception
            {
                RiskManager.INSTANCE.inspect(single);
            }
        };
        single.setPrice(new BigDecimal("0.01"));
        RiskManager.INSTANCE.inspect(single);
        single.setPrice(new BigDecimal("0.02"));
        RiskManager.INSTANCE.inspect(single);
        // test order replace
        final OrderReplace replace = ClientTest.createOrderReplace();
        establishData(replace.getInstrument().getSymbol());
        replace.setPrice(new BigDecimal("0.001"));
        new ExpectedFailure<UserLimitViolation>(Messages.LESS_THAN_A_PENNY){
            @Override
            protected void run()
                    throws Exception
            {
                RiskManager.INSTANCE.inspect(replace);
            }
        };
        replace.setPrice(new BigDecimal("0.01"));
        RiskManager.INSTANCE.inspect(replace);
        replace.setPrice(new BigDecimal("0.02"));
        RiskManager.INSTANCE.inspect(replace);
        // test FIX order
        final FIXOrder order = ClientTest.createOrderFIX();
        quickfix.field.Symbol symbolField = new quickfix.field.Symbol();
        order.getMessage().getField(symbolField);
        establishData(symbolField.getValue());
        order.getMessage().setField(new Price(new BigDecimal("0.001")));
        order.getMessage().setField(new Quantity(new BigDecimal("50")));
        order.getMessage().setField(new quickfix.field.OrderID("some-order-id"));
        new ExpectedFailure<UserLimitViolation>(Messages.LESS_THAN_A_PENNY){
            @Override
            protected void run()
                    throws Exception
            {
                RiskManager.INSTANCE.inspect(order);
            }
        };
    }
    private void establishData(String inSymbol)
            throws Exception
    {
        establishSymbolData(inSymbol);
        TradeEvent trade = TradeEventBuilder.equityTradeEvent().withInstrument(new Equity(inSymbol))
                                                               .withExchange("some exchange")
                                                               .withPrice(new BigDecimal(1000))
                                                               .withSize(new BigDecimal(1000))
                                                               .withTradeDate("now").create();
        Data data = marketdata.get(inSymbol);
        if(data == null) {
            
        }
    }
    private void establishSymbolData(String inSymbol)
            throws Exception
    {
        establishSymbolData(inSymbol,
                            new BigDecimal(1000),
                            new BigDecimal(1000),
                            new BigDecimal(1000),
                            new BigDecimal(1000));
    }
    private void establishSymbolData(String inSymbol,
                                     BigDecimal inMaximumTradeValue,
                                     BigDecimal inMaximumPosition,
                                     BigDecimal inMaximumDeviationFromLast,
                                     BigDecimal inMaximumDeviationFromMid)
            throws Exception
    {
        SymbolDataCollection symbolData = new SymbolDataCollection();
        symbolData.add(new SymbolData(inSymbol,
                                      inMaximumTradeValue,
                                      inMaximumPosition,
                                      inMaximumDeviationFromLast,
                                      inMaximumDeviationFromMid));
        symbolData.store();
    }
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
            return null;
        }
        @Override
        public BigDecimal getFuturePositionAsOf(Date inDate,
                                                Future inEquity)
                throws ConnectionException
        {
            return null;
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
            return null;
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
        private Properties userData = new Properties();
    };
    private RiskManager.MarketDataProvider marketDataProvider = new RiskManager.MarketDataProvider() {
        @Override
        public Data getData(String inSymbol)
        {
            return marketdata.get(inSymbol);
        }
    };
    private Map<String,Data> marketdata = new HashMap<String,Data>(); 
}
