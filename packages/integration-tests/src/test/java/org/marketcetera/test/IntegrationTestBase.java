package org.marketcetera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.marketcetera.core.PlatformServices.divisionContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.PriceQtyTuple;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.trade.Receiver;
import org.marketcetera.test.trade.Sender;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Messages;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.service.OrderSummaryService;
import org.marketcetera.trade.service.ReportService;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.google.common.collect.Lists;

import junitparams.JUnitParamsRunner;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;

/* $License$ */

/**
 * Provides common test behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest(classes=IntegrationTestConfiguration.class)
@ComponentScan(basePackages={"org.marketcetera"})
@EntityScan(basePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
public class IntegrationTestBase
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "{} beginning setup",
                              name.getMethodName());
        tradeMessages.clear();
        tradeService.addTradeMessageListener(tradeMessageListener);
        verifyAllBrokersReady();
        SLF4JLoggerProxy.info(this,
                              "{} beginning",
                              name.getMethodName());
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "{} cleanup beginning",
                              name.getMethodName());
        try {
            tradeService.removeTradeMessageListener(tradeMessageListener);
            tradeMessages.clear();
        } finally {
            SLF4JLoggerProxy.info(this,
                                  "{} done",
                                  name.getMethodName());
        }
    }
    /**
     * Verify that the order status is NEW for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatusNew(final OrderID inRootOrderId,
                                               final OrderID inOrderId)
            throws Exception
    {
        verifyOrderSummaryStatus(inRootOrderId,
                                 inOrderId,
                                 OrderStatus.New);
    }
    /**
     * Verify that the order status is FILLED for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatusFilled(final OrderID inRootOrderId,
                                                  final OrderID inOrderId)
            throws Exception
    {
        verifyOrderSummaryStatus(inRootOrderId,
                                 inOrderId,
                                 OrderStatus.Filled);
    }
    /**
     * Verify that the order status is PARTIALLY FILLED for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatusPartiallyFilled(final OrderID inRootOrderId,
                                                           final OrderID inOrderId)
            throws Exception
    {
        verifyOrderSummaryStatus(inRootOrderId,
                                 inOrderId,
                                 OrderStatus.PartiallyFilled);
    }
    /**
     * Verify that the order status for the given root/order id pair exists.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     * @param inOrderId an <code>OrderID</code> value
     * @param inExpectedOrderStatus an <code>OrderStatus</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyOrderSummaryStatus(final OrderID inRootOrderId,
                                            final OrderID inOrderId,
                                            final OrderStatus inExpectedOrderStatus)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                OrderSummary orderStatus = orderSummaryService.findByRootOrderIdAndOrderId(inRootOrderId,
                                                                                          inOrderId);
                if(orderStatus == null) {
                    return false;
                }
                return orderStatus.getOrderStatus() == inExpectedOrderStatus;
            }
        },10);
    }
    /**
     * Wait for and return the next trade message.
     *
     * @return a <code>TradeMessage</code> value
     * @throws Exception if the message does not arrive
     */
    protected TradeMessage waitForNextTradeMessage()
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !tradeMessages.isEmpty();
            }
        },10);
        return tradeMessages.remove(0);
    }
    /**
     * 
     *
     *
     * @param inSessionId
     * @return
     * @throws Exception
     */
    protected Message waitForReceiverMessage(SessionID inSessionId)
            throws Exception
    {
        return receiver.getNextApplicationMessage(inSessionId);
    }
    /**
     * 
     *
     *
     * @param inSessionId
     * @return
     * @throws Exception
     */
    protected Message waitForSenderMessage(SessionID inSessionId)
            throws Exception
    {
        return sender.getNextApplicationMessage(inSessionId);
    }
    /**
     * Verify that all brokers are connected.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyAllBrokersReady()
            throws Exception
    {
        for(FixSession fixSession : brokerService.findFixSessions()) {
            verifySessionLoggedOn(new BrokerID(fixSession.getBrokerId()));
        }
    }
    /**
     * Verify that the given session is disabled.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionDisabled(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return !status.getStatus().isEnabled();
            }
        });
    }
    /**
     * Verify that the given session is deleted.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionDeleted(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                return status == null || status.getStatus() == FixSessionStatus.DELETED;
            }
        });
    }
    /**
     * Verify that the given session is logged on.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOn(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return status.getLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is logged on.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOn(final SessionID inSessionId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Session session = Session.lookupSession(inSessionId);
                if(session == null) {
                    return false;
                }
                if(!session.isLoggedOn()) {
                    return false;
                }
                if(DateTime.now().isBefore(new DateTime(session.getStartTime()).plusSeconds(2))) {
                    return false;
                }
                return true;
            }
        });
    }
    /**
     * Verify that the given session is logged off.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOff(final SessionID inSessionId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Session session = Session.lookupSession(inSessionId);
                return session == null || !session.isLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is logged off.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionLoggedOff(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return !status.getLoggedOn();
            }
        });
    }
    /**
     * Verify that the given session is enabled.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void verifySessionEnabled(final BrokerID inBrokerId)
            throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                BrokerStatus status = brokerService.getBrokerStatus(inBrokerId);
                if(status == null) {
                    return false;
                }
                return status.getStatus().isEnabled();
            }
        });
    }
    /**
     * Verifies that the given broker matches the given status.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inExpectedStatus a <code>Boolean</code> value or <code>null</code>
     * @throws Exception if an unexpected error occurs
     */
    protected void verifyBrokerStatus(BrokerID inBrokerId,
                                      Boolean inExpectedStatus)
            throws Exception
    {
        BrokerStatus brokerStatus = brokerService.getBrokerStatus(inBrokerId);
        if(inExpectedStatus == null) {
            assertNull(brokerStatus);
        } else {
            assertEquals(inExpectedStatus,
                         brokerStatus.getLoggedOn());
        }
    }
    /**
     * Get an initiator broker.
     *
     * @return a <code>Broker</code> value
     */
    protected Broker getInitiator()
    {
        for(FixSession fixSession : brokerService.findFixSessions(false,1,1)) {
            return brokerService.getBroker(new BrokerID(fixSession.getBrokerId()));
        }
        throw new UnsupportedOperationException("No initiators configured!");
    }
    /**
     * Get the trader user.
     *
     * @return a <code>User</code> value
     */
    protected User getTraderUser()
    {
        User trader = userService.findByName("trader");
        assertNotNull(trader);
        return trader;
    }
    /**
     * Get the FIX versions for test parameters.
     *
     * @return an <code>Object</code> value
     */
    protected Object fixVersionParameters()
    {
        List<Object> results = Lists.newArrayList();
        for(FIXVersion fixVersion : getFixVersionList()) {
            results.add(new Object[] { fixVersion });
        }
        return results.toArray();
    }
    /**
     * Generates a message with the given comma-separated fields.
     *
     * @param inHeaderFields a <code>String</code> value
     * @param inFields a <code>String</code> value
     * @param inMsgType a <code>String</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static Message buildMessage(String inHeaderFields,
                                          String inFields,
                                          String inMsgType,
                                          FIXMessageFactory inFactory)
            throws Exception
    {
        Map<Integer,String> fields = new HashMap<>();
        Message message = inFactory.createMessage(inMsgType);
        String[] pairs = inHeaderFields.split(",");
        if(pairs != null) {
            for(String pair : pairs) {
                pair = StringUtils.trimToNull(pair);
                String[] components = pair.split("=");
                fields.put(Integer.parseInt(components[0]),
                           components[1]);
            }
        }
        for(Map.Entry<Integer,String> entry : fields.entrySet()) {
            message.getHeader().setString(entry.getKey(),
                                          entry.getValue());
        }
        fields.clear();
        pairs = inFields.split(",");
        for(String pair : pairs) {
            pair = StringUtils.trimToNull(pair);
            String[] components = pair.split("=");
            fields.put(Integer.parseInt(components[0]),
                       components[1]);
        }
        for(Map.Entry<Integer,String> entry : fields.entrySet()) {
            message.setString(entry.getKey(),
                              entry.getValue());
        }
        return message;
    }
    /**
     * Generate an execution report based on the given inputs.
     *
     * @param inOrder a <code>Message</code> value
     * @param inOrderData an <code>OrderData</code> value
     * @param inOrderId a <code>String</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inExecutionType an <code>ExecutionType</code> value
     * @param inFactory a <code>FIXMessageFactory</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Message generateExecutionReport(Message inOrder,
                                              CalculatedOrderData inOrderData,
                                              String inOrderId,
                                              org.marketcetera.trade.OrderStatus inOrderStatus,
                                              ExecutionType inExecutionType,
                                              FIXMessageFactory inFactory)
            throws Exception
    {
        return generateExecutionReport(inOrder,
                                       inOrderData,
                                       inOrderId,
                                       inOrder.getString(quickfix.field.ClOrdID.FIELD),
                                       inOrder.isSetField(quickfix.field.OrigClOrdID.FIELD)?inOrder.getString(quickfix.field.OrigClOrdID.FIELD):null,
                                       inOrderStatus,
                                       inExecutionType,
                                       inFactory);
    }
    /**
     * 
     *
     *
     * @param inOrder
     * @param inOrderData
     * @param inClOrdId
     * @param inOrigClOrdId
     * @param inOrderId
     * @param inOrderStatus
     * @param inExecutionType
     * @param inFactory
     * @return
     * @throws Exception
     */
    protected Message generateExecutionReport(Message inOrder,
                                              CalculatedOrderData inOrderData,
                                              String inOrderId,
                                              String inClOrdId,
                                              String inOrigClOrdId,
                                              org.marketcetera.trade.OrderStatus inOrderStatus,
                                              ExecutionType inExecutionType,
                                              FIXMessageFactory inFactory)
            throws Exception
    {
        boolean commaNeeded = false;
        StringBuilder body = new StringBuilder();
        if(inOrder.isSetField(quickfix.field.Account.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Account.FIELD).append('=').append(inOrder.getString(quickfix.field.Account.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.AvgPx.FIELD).append('=').append(inOrderData.calculateAveragePrice().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ClOrdID.FIELD).append('=').append(inClOrdId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.CumQty.FIELD).append('=').append(inOrderData.calculateCumQty().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecID.FIELD).append('=').append(PlatformServices.generateId()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecTransType.FIELD).append('=').append(quickfix.field.ExecTransType.NEW); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LastPx.FIELD).append('=').append(inOrderData.calculateLastPx().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LastQty.FIELD).append('=').append(inOrderData.calculateLastQty().toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrderID.FIELD).append('=').append(inOrderId); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrderQty.FIELD).append('=').append(inOrder.getDecimal(quickfix.field.OrderQty.FIELD).toPlainString()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrdStatus.FIELD).append('=').append(inOrderStatus.getFIXValue()); commaNeeded = true;
        if(inOrder.isSetField(quickfix.field.OrdType.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrdType.FIELD).append('=').append(inOrder.getChar(quickfix.field.OrdType.FIELD)); commaNeeded = true;
        }
        if(inOrigClOrdId != null) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.OrigClOrdID.FIELD).append('=').append(inOrigClOrdId); commaNeeded = true;
        }
        if(inOrder.isSetField(quickfix.field.Price.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Price.FIELD).append('=').append(inOrder.getDecimal(quickfix.field.Price.FIELD).toPlainString()); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.Side.FIELD).append('=').append(inOrder.getChar(quickfix.field.Side.FIELD)); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.Symbol.FIELD).append('=').append(inOrder.getString(quickfix.field.Symbol.FIELD)); commaNeeded = true;
        if(inOrder.isSetField(quickfix.field.Text.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.Text.FIELD).append('=').append(inOrder.getString(quickfix.field.Text.FIELD)); commaNeeded = true;
        }
        if(inOrder.isSetField(quickfix.field.TimeInForce.FIELD)) {
            if(commaNeeded) { body.append(','); } body.append(quickfix.field.TimeInForce.FIELD).append('=').append(inOrder.getChar(quickfix.field.TimeInForce.FIELD)); commaNeeded = true;
        }
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.TransactTime.FIELD).append('=').append(TimeFactoryImpl.FULL_MILLISECONDS.print(System.currentTimeMillis())); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.ExecType.FIELD).append('=').append(inExecutionType.getFIXValue()); commaNeeded = true;
        if(commaNeeded) { body.append(','); } body.append(quickfix.field.LeavesQty.FIELD).append('=').append(inOrderData.calculateLeavesQty().toPlainString()); commaNeeded = true;
        return buildMessage(MsgType.FIELD+"="+MsgType.EXECUTION_REPORT,
                            body.toString(),
                            MsgType.EXECUTION_REPORT,
                            inFactory);
    }
    /**
     * Get list of FIX versions to use for testing.
     *
     * @return a <code>List&lt;FIXVersion&gt;</code> value
     */
    private List<FIXVersion> getFixVersionList()
    {
        List<FIXVersion> fixVersions = Lists.newArrayList();
        for(FIXVersion fixVersion : FIXVersion.values()) {
            if(fixVersion != FIXVersion.FIX_SYSTEM) {
                fixVersions.add(fixVersion);
            }
        }
        return fixVersions;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public class CalculatedOrderData
    {
        /**
         * Add a price/qty pair.
         *
         * @param inPrice a <code>BigDecimal</code> value
         * @param inQty a <code>BigDecimal</code> value
         */
        public void add(BigDecimal inPrice,
                        BigDecimal inQty)
        {
            tuples.add(new PriceQtyTuple(inPrice,inQty));
        }
        /**
         * Add the given execution to the calculation list.
         *
         * @param inExecution a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public void addExecution(Message inExecution)
                throws Exception
        {
            if(inExecution.isSetField(quickfix.field.LastPx.FIELD) && inExecution.isSetField(quickfix.field.LastQty.FIELD)) {
                add(inExecution.getDecimal(quickfix.field.LastPx.FIELD),
                    inExecution.getDecimal(quickfix.field.LastQty.FIELD));
            }
        }
        /**
         * Calculate the average price from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateAveragePrice()
        {
            BigDecimal result = BigDecimal.ZERO;
            BigDecimal totalQty = BigDecimal.ZERO;
            BigDecimal extendedQty = BigDecimal.ZERO;
            for(PriceQtyTuple amount : tuples) {
                totalQty = totalQty.add(amount.getQty());
                extendedQty = extendedQty.add(amount.getPrice().multiply(amount.getQty()));
            }
            if(totalQty.compareTo(BigDecimal.ZERO) != 0) {
                result = extendedQty.divide(totalQty,
                                            divisionContext);
            }
            return result;
        }
        /**
         * Calculate the cumulative quantity from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateCumQty()
        {
            BigDecimal result = BigDecimal.ZERO;
            for(PriceQtyTuple amount : tuples) {
                result = result.add(amount.getQty());
            }
            return result;
        }
        /**
         * Calculate the leaves quantity from the order data.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLeavesQty()
        {
            return orderQuantity.subtract(calculateCumQty());
        }
        /**
         * Calculate the last price from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLastPx()
        {
            BigDecimal result = BigDecimal.ZERO;
            if(!tuples.isEmpty()) {
                result = tuples.get(tuples.size()-1).getPrice();
            }
            return result;
        }
        /**
         * Calculate the last qty from the collected quantities.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal calculateLastQty()
        {
            BigDecimal result = BigDecimal.ZERO;
            if(!tuples.isEmpty()) {
                result = tuples.get(tuples.size()-1).getQty();
            }
            return result;
        }
        /**
         * Get the orderPrice value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOrderPrice()
        {
            return orderPrice;
        }
        /**
         * Sets the orderPrice value.
         *
         * @param inOrderPrice a <code>BigDecimal</code> value
         */
        public void setOrderPrice(BigDecimal inOrderPrice)
        {
            orderPrice = inOrderPrice;
        }
        /**
         * Get the orderQuantity value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOrderQuantity()
        {
            return orderQuantity;
        }
        /**
         * Sets the orderQuantity value.
         *
         * @param inOrderQuantity a <code>BigDecimal</code> value
         */
        public void setOrderQuantity(BigDecimal inOrderQuantity)
        {
            orderQuantity = inOrderQuantity;
        }
        /**
         * Generate an order status request for the given order sent from the given session.
         *
         * @param inOrderMessage a <code>Message</code> value
         * @param inSessionId a <code>SessionID</code> value
         * @return a <code>Message</code> value
         */
        public Message generateOrderStatusRequest(Message inOrderMessage,
                                                  SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inOrderMessage);
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_STATUS_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            orderMessages.add(order);
            return order;
        }
        public Message generateOrderCancel(Message inOrderMessage,
                                           SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSessionId.getBeginString());
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            order.setField(new quickfix.field.OrigClOrdID(inOrderMessage.getString(quickfix.field.ClOrdID.FIELD)));
            order.setField(new quickfix.field.ClOrdID(PlatformServices.generateId()));
            orderMessages.add(order);
            return order;
        }
        public Message generateOrderReplace(Message inOrderMessage,
                                            SessionID inSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSessionId);
            FIXMessageFactory factory = version.getMessageFactory();
            Message order = factory.createMessage(quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST);
            FIXMessageUtil.fillFieldsFromExistingMessage(order,
                                                         inOrderMessage,
                                                         FIXMessageUtil.getDataDictionary(version),
                                                         false);
            order.setField(new quickfix.field.OrigClOrdID(inOrderMessage.getString(quickfix.field.ClOrdID.FIELD)));
            order.setField(new quickfix.field.ClOrdID(PlatformServices.generateId()));
            orderMessages.add(order);
            return order;
        }
        /**
         * Generate an order with the given instrument targeted to the given session.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inSenderSessionId a <code>SessionID</code> value
         * @return a <code>Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        public Message generateOrder(Instrument inInstrument,
                                     SessionID inSenderSessionId)
                throws Exception
        {
            FIXVersion version = FIXVersion.getFIXVersion(inSenderSessionId);
            FIXMessageFactory factory = version.getMessageFactory();
            String account = PlatformServices.generateId();
            String clOrdId = PlatformServices.generateId();
            StringBuilder body = new StringBuilder();
            body.append(quickfix.field.Account.FIELD).append('=').append(account).append(',');
            body.append(quickfix.field.ClOrdID.FIELD).append('=').append(clOrdId).append(',');
            body.append(quickfix.field.HandlInst.FIELD).append('=').append(quickfix.field.HandlInst.MANUAL_ORDER).append(',');
            body.append(quickfix.field.OrderQty.FIELD).append('=').append(orderQuantity.toPlainString()).append(',');
            if(orderType != null) {
                body.append(quickfix.field.OrdType.FIELD).append('=').append(orderType.getFIXValue()).append(',');
            }
            if(orderPrice != null) {
                body.append(quickfix.field.Price.FIELD).append('=').append(orderPrice.toPlainString()).append(',');
            }
            body.append(quickfix.field.Side.FIELD).append('=').append(side.getFIXValue()).append(',');
            Message order = buildMessage("35="+MsgType.ORDER_SINGLE,
                                         body.toString(),
                                         MsgType.ORDER_SINGLE,
                                         factory);
            order.setField(new quickfix.field.TransactTime(new Date()));
            InstrumentToMessage<?> instrumentFunction = InstrumentToMessage.SELECTOR.forInstrument(inInstrument);
            DataDictionary fixDictionary = FIXMessageUtil.getDataDictionary(inSenderSessionId);
            if(!instrumentFunction.isSupported(fixDictionary,
                                               quickfix.field.MsgType.ORDER_SINGLE)) {
                throw new I18NException(Messages.UNSUPPORTED_INSTRUMENT);
            }
            instrumentFunction.set(inInstrument,
                                   fixDictionary,
                                   quickfix.field.MsgType.ORDER_SINGLE,
                                   order);
            orderMessages.add(order);
            return order;
        }
        /**
         * Create a new CalculatedOrderData instance.
         *
         * @param inOrderQuantity a <code>BigDecimal</code> value
         * @param inOrderPrice a <code>BigDecimal</code> value or <code>null</code>
         * @param inOrderType an <code>OrderType</code> value
         * @param inSide a <code>Side</code> value
         */
        public CalculatedOrderData(BigDecimal inOrderQuantity,
                                   BigDecimal inOrderPrice,
                                   OrderType inOrderType,
                                   Side inSide)
        {
            orderQuantity = inOrderQuantity;
            orderPrice = inOrderPrice;
            orderType = inOrderType;
            side = inSide;
        }
        /**
         * collection of price/qty tuples
         */
        private final List<PriceQtyTuple> tuples = new ArrayList<>();
        /**
         * order quantity value
         */
        protected BigDecimal orderQuantity;
        /**
         * 
         */
        protected BigDecimal orderPrice;
        protected String orderId;
        protected final OrderType orderType;
        protected final Side side;
        protected final Deque<Message> orderMessages = Lists.newLinkedList();
        protected final List<Message> executionMessages = Lists.newArrayList();
    }
    /**
     * holds trade messages received during a test
     */
    protected final List<TradeMessage> tradeMessages = Lists.newArrayList();
    /**
     * listens for trade messages received during a test
     */
    protected final TradeMessageListener tradeMessageListener = new TradeMessageListener() {
        @Override
        public void receiveTradeMessage(TradeMessage inTradeMessage)
        {
            synchronized(tradeMessages) {
                tradeMessages.add(inTradeMessage);
                tradeMessages.notifyAll();
            }
        }
    };
    /**
     * test FIX brokers (receives requests from initiators)
     */
    @Autowired
    protected Receiver receiver;
    /**
     * test FIX brokers (sends requests to acceptors)
     */
    @Autowired
    protected Sender sender;
    /**
     * provides access to user services
     */
    @Autowired
    protected UserService userService;
    /**
     * provides access to report services
     */
    @Autowired
    protected ReportService reportService;
    /**
     * provides access to broker services
     */
    @Autowired
    protected BrokerService brokerService;
    /**
     * provides access to trade services
     */
    @Autowired
    protected TradeService tradeService;
    /**
     * provides access to order summary services
     */
    @Autowired
    protected OrderSummaryService orderSummaryService;
    /**
     * test artifact used to identify the current test case
     */
    @Rule
    public TestName name = new TestName();
    /**
     * rule used to load test context
     */
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    /**
     * test spring method rule
     */
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
}
