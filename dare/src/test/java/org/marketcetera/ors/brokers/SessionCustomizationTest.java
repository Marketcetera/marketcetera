package org.marketcetera.ors.brokers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.marketcetera.brokers.BrokerConstants;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.DareTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.time.DateService;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Tests session customizations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionCustomizationTest
        extends DareTestBase
{
    /**
     * Test incoming and outgoing order modifiers.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOrderModifiers()
            throws Exception
    {
        // create a new broker with a session customization
        int sessionIndex = counter.incrementAndGet();
        createRemoteReceiverSession(sessionIndex);
        quickfix.SessionID senderSessionId = createInitiatorSession(sessionIndex);
        FIXVersion senderFixVersion = FIXVersion.getFIXVersion(senderSessionId);
        quickfix.SessionID targetSessionId = FIXMessageUtil.getReversedSessionId(senderSessionId);
        FixSession session = brokerService.getActiveFixSession(senderSessionId).getFixSession();
        BrokerID brokerId = new BrokerID(session.getBrokerId());
        verifySessionLoggedOn(brokerId);
        TextModifier orderModifier = new TextModifier();
        TextModifier responseModifier = new TextModifier();
        testSessionCustomization.getOrderModifiers().add(orderModifier);
        testSessionCustomization.getResponseModifiers().add(responseModifier);
        modifyFixSession(session,
                     testSessionCustomization);
        Instrument inInstrument = generateInstrument();
        BigDecimal inOrderQty = new BigDecimal(20);
        OrderSingle order = Factory.getInstance().createOrderSingle();
        BigDecimal orderPrice = new BigDecimal(100);
        order.setBrokerID(brokerId);
        order.setInstrument(inInstrument);
        order.setOrderType(OrderType.Limit);
        order.setPrice(orderPrice);
        order.setQuantity(inOrderQty);
        order.setSide(Side.Buy);
        client.sendOrder(order);
        quickfix.Message receivedOrder = waitForAndVerifySenderMessage(senderSessionId,
                                                                       quickfix.field.MsgType.ORDER_SINGLE);
        assertTrue(receivedOrder.isSetField(quickfix.field.Text.FIELD));
        assertEquals(orderModifier.getTextValue(),
                     receivedOrder.getString(quickfix.field.Text.FIELD));
        String orderId = generateId();
        FIXMessageFactory fixMessageFactory = FIXVersion.getFIXVersion(senderSessionId).getMessageFactory();
        quickfix.Message orderPendingNew = buildMessage("35=8",
                                                        "6=0,11="+order.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+orderId+",38="+inOrderQty.toPlainString()+",39="+OrderStatus.PendingNew.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+orderPrice.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingNew.getFIXValue()+",151="+inOrderQty.toPlainString(),
                                                        quickfix.field.MsgType.EXECUTION_REPORT,
                                                        fixMessageFactory);
        orderPendingNew.setField(new quickfix.field.TransactTime(DateService.toUtcDateTime(new Date(System.currentTimeMillis()-1000))));
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedOrder),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     orderPendingNew);
        orderPendingNew = senderFixVersion.getMessageFactory().getMsgAugmentor().executionReportAugment(orderPendingNew);
        quickfix.Session.sendToTarget(orderPendingNew,
                                      targetSessionId);
        verifyOrderStatus(order.getOrderID(),
                          order.getOrderID(),
                          OrderStatus.PendingNew);
        TradeMessage executionReportHolder = reports.getFirst();
        quickfix.Message executionReport = ((HasFIXMessage)executionReportHolder).getMessage();
        assertTrue(executionReport.isSetField(quickfix.field.Text.FIELD));
        assertEquals(responseModifier.getTextValue(),
                     executionReport.getString(quickfix.field.Text.FIELD));
        reports.clear();
        // send new
        quickfix.Message orderNew = buildMessage("35=8",
                                                 "6=0,11="+order.getOrderID()+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+orderId+",38="+inOrderQty.toPlainString()+",39="+OrderStatus.New.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+orderPrice.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.New.getFIXValue()+",151="+inOrderQty.toPlainString(),
                                                 quickfix.field.MsgType.EXECUTION_REPORT,
                                                 fixMessageFactory);
        orderNew.setField(new quickfix.field.TransactTime(DateService.toUtcDateTime(new Date(System.currentTimeMillis()-1000))));
        InstrumentToMessage.SELECTOR.forInstrument(inInstrument).set(inInstrument,
                                                                     FIXMessageUtil.getDataDictionary(receivedOrder),
                                                                     quickfix.field.MsgType.EXECUTION_REPORT,
                                                                     orderNew);
        orderNew = senderFixVersion.getMessageFactory().getMsgAugmentor().executionReportAugment(orderNew);
        quickfix.Session.sendToTarget(orderNew,
                                      targetSessionId);
        verifyOrderStatus(order.getOrderID(),
                          order.getOrderID(),
                          OrderStatus.New);
        executionReportHolder = reports.getFirst();
        executionReport = ((HasFIXMessage)executionReportHolder).getMessage();
        assertTrue(executionReport.isSetField(quickfix.field.Text.FIELD));
        assertEquals(responseModifier.getTextValue(),
                     executionReport.getString(quickfix.field.Text.FIELD));
    }
    // TODO test incoming admin message
    // TODO test outgoing admin message
    // TODO test intercepted outgoing app message
    // TODO test intercepted incoming app message
    /**
     * Modify the given FIX session and add the given customization.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inSessionCustomization a <code>TestSessionCustomization</code> value
     * @throws Exception if an error occurs modifying the FIX session
     */
    private void modifyFixSession(FixSession inFixSession,
                                  TestSessionCustomization inSessionCustomization)
            throws Exception
    {
        quickfix.SessionID sessionId = new quickfix.SessionID(inFixSession.getSessionId());
        BrokerID brokerId = new BrokerID(inFixSession.getBrokerId());
        fixSessionProvider.disableSession(sessionId);
        verifySessionDisabled(brokerId);
        inFixSession.getSessionSettings().put(BrokerConstants.sessionCustomizationKey,
                                              inSessionCustomization.getName());
        inFixSession = fixSessionProvider.save(inFixSession);
        fixSessionProvider.enableSession(sessionId);
        verifySessionLoggedOn(brokerId);
    }
    /**
     * Test modifier which modifies the value of the text field for certain message types.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TextModifier
            implements MessageModifier
    {
        /* (non-Javadoc)
         * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
         */
        @Override
        public boolean modify(ServerFixSession inServerFixSession,
                              quickfix.Message inMessage)
        {
            if(FIXMessageUtil.isOrderSingle(inMessage) || FIXMessageUtil.isExecutionReport(inMessage)) {
                inMessage.setString(quickfix.field.Text.FIELD,
                                    textValue);
                return true;
            }
            return false;
        }
        /**
         * Get the textValue value.
         *
         * @return a <code>String</code> value
         */
        private String getTextValue()
        {
            return textValue;
        }
        /**
         * text value to apply to messages
         */
        private final String textValue = UUID.randomUUID().toString();
    }
    /**
     * provides access to the test session customization
     */
    @Autowired
    private TestSessionCustomization testSessionCustomization;
}
