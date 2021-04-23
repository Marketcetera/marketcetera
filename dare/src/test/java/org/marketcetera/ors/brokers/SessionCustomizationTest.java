package org.marketcetera.ors.brokers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Deque;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.brokers.BrokerConstants;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.MessageIntercepted;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.test.DareTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.time.DateService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

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
     * Runs before each test.
     * 
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        testSessionCustomization.getOrderModifiers().clear();
        testSessionCustomization.getResponseModifiers().clear();
    }
    @Test
    public void testOutgoingMessages()
            throws Exception
    {
        doCustomizationTest(false,
                            false);
        doCustomizationTest(false,
                            true);
        doCustomizationTest(true,
                            false);
        doCustomizationTest(true,
                            true);
    }
    private void doCustomizationTest(boolean inIsIntercepted,
                                     boolean isThrowException)
            throws Exception
    {
        int sessionIndex = counter.incrementAndGet();
        createRemoteReceiverSession(sessionIndex);
        quickfix.SessionID senderSessionId = createInitiatorSession(sessionIndex);
        quickfix.SessionID targetSessionId = FIXMessageUtil.getReversedSessionId(senderSessionId);
        FixSession session = brokerService.getActiveFixSession(senderSessionId).getFixSession();
        BrokerID brokerId = new BrokerID(session.getBrokerId());
        verifySessionLoggedOn(brokerId);
        MockMessageModifier logonModifier = new MockMessageModifier();
        logonModifier.setModifyText(true);
        logonModifier.setModifyUsername(true);
        logonModifier.setInterceptMessage(inIsIntercepted);
        MessageRecorder outgoingMessageRecorder = new MessageRecorder();
        MessageRecorder incomingMessageRecorder = new MessageRecorder();
        testSessionCustomization.getOrderModifiers().add(logonModifier);
        testSessionCustomization.getOrderModifiers().add(outgoingMessageRecorder);
        testSessionCustomization.getResponseModifiers().add(logonModifier);
        testSessionCustomization.getResponseModifiers().add(incomingMessageRecorder);
        verifyNoIncomingMessageInterceptedEvents();
        verifyNoOutgoingMessageInterceptedEvents();
        modifyFixSession(session,
                         testSessionCustomization);
        // outgoing admin message test
        quickfix.Message outgoingLogonMessage = outgoingMessageRecorder.getFirstRecordedMessage();
        assertTrue(FIXMessageUtil.isLogon(outgoingLogonMessage));
        assertTrue(outgoingLogonMessage.isSetField(quickfix.field.Username.FIELD));
        assertEquals(logonModifier.getUsernameValue(),
                     outgoingLogonMessage.getString(quickfix.field.Username.FIELD));
        verifyNoOutgoingMessageInterceptedEvents();
        // incoming admin message test
        quickfix.Message incomingLogonMessage = incomingMessageRecorder.getFirstRecordedMessage();
        assertTrue(FIXMessageUtil.isLogon(incomingLogonMessage));
        assertTrue(incomingLogonMessage.isSetField(quickfix.field.Username.FIELD));
        assertEquals(logonModifier.getUsernameValue(),
                     incomingLogonMessage.getString(quickfix.field.Username.FIELD));
        verifyNoIncomingMessageInterceptedEvents();
        // outgoing app message test
        resetMessageInterceptedEvents();
        outgoingMessageRecorder.reset();
        OrderSingle outgoingOrder = sendOrder(brokerId);
        quickfix.Message outgoingOrderMessage = outgoingMessageRecorder.getFirstRecordedMessage();
        assertTrue("Expected order single: " + FIXMessageUtil.toHumanDelimitedString(outgoingOrderMessage),
                   FIXMessageUtil.isOrderSingle(outgoingOrderMessage));
        assertEquals(outgoingOrder.getOrderID().getValue(),
                     outgoingOrderMessage.getString(quickfix.field.ClOrdID.FIELD));
        assertTrue(outgoingOrderMessage.isSetField(quickfix.field.Text.FIELD));
        assertEquals(logonModifier.getTextValue(),
                     outgoingOrderMessage.getString(quickfix.field.Text.FIELD));
        if(inIsIntercepted) {
            waitForOutgoingMessageInterceptedEvent();
        } else {
            verifyNoOutgoingMessageInterceptedEvents();
        }
        // incoming app message test
        resetMessageInterceptedEvents();
        incomingMessageRecorder.reset();
        quickfix.Message report = ackOrder(targetSessionId); 
        quickfix.Message incomingReportMessage = incomingMessageRecorder.getFirstRecordedMessage();
        assertTrue(FIXMessageUtil.isExecutionReport(incomingReportMessage));
        assertEquals(report.getString(quickfix.field.ClOrdID.FIELD),
                     incomingReportMessage.getString(quickfix.field.ClOrdID.FIELD));
        assertTrue(incomingReportMessage.isSetField(quickfix.field.Text.FIELD));
        assertEquals(logonModifier.getTextValue(),
                     incomingReportMessage.getString(quickfix.field.Text.FIELD));
        if(inIsIntercepted) {
            waitForIncomingMessageInterceptedEvent();
        } else {
            verifyNoIncomingMessageInterceptedEvents();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.test.DareTestBase#getFixVersion()
     */
    @Override
    protected FIXVersion getFixVersion()
    {
        return FIXVersion.FIX50SP2;
    }
    private quickfix.Message ackOrder(quickfix.SessionID inTargetSessionId)
            throws Exception
    {
        String clOrdId = generateId();
        BigDecimal orderQty = BigDecimal.ONE;
        BigDecimal orderPrice = BigDecimal.TEN;
        quickfix.SessionID senderSessionId = FIXMessageUtil.getReversedSessionId(inTargetSessionId);
        FIXVersion senderFixVersion = FIXVersion.getFIXVersion(senderSessionId);
        String brokerOrderId = generateId();
        FIXMessageFactory fixMessageFactory = senderFixVersion.getMessageFactory();
        quickfix.Message orderPendingNew = buildMessage("35=8",
                                                        "6=0,11="+clOrdId+",14=0,15=USD,17="+generateId()+",20=0,21=3,22=1,31=0,32=0,37="+brokerOrderId+",38="+orderQty.toPlainString()+",39="+OrderStatus.PendingNew.getFIXValue()+",40="+OrderType.Limit.getFIXValue()+",44="+orderPrice.toPlainString()+",54="+Side.Buy.getFIXValue()+",59="+TimeInForce.GoodTillCancel.getFIXValue()+",60=20141210-15:04:55.098,150="+ExecutionType.PendingNew.getFIXValue()+",151="+orderQty.toPlainString(),
                                                        quickfix.field.MsgType.EXECUTION_REPORT,
                                                        fixMessageFactory);
        orderPendingNew.setField(new quickfix.field.TransactTime(DateService.toUtcDateTime(new Date(System.currentTimeMillis()-1000))));
        InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                   FIXMessageUtil.getDataDictionary(senderFixVersion),
                                                                   quickfix.field.MsgType.EXECUTION_REPORT,
                                                                   orderPendingNew);
        orderPendingNew = senderFixVersion.getMessageFactory().getMsgAugmentor().executionReportAugment(orderPendingNew);
        quickfix.Session.sendToTarget(orderPendingNew,
                                      inTargetSessionId);
        return orderPendingNew;
    }
    /**
     * Generate and send an order.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return an <code>OrderSingle</code> value containing the order sent out
     * @throws Exception if an error occurs building and sending the message
     */
    private OrderSingle sendOrder(BrokerID inBrokerId)
            throws Exception
    {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        BigDecimal orderPrice = new BigDecimal(100);
        order.setBrokerID(inBrokerId);
        order.setInstrument(generateInstrument());
        order.setOrderType(OrderType.Limit);
        order.setPrice(orderPrice);
        order.setQuantity(BigDecimal.TEN);
        order.setSide(Side.Buy);
        client.sendOrder(order);
        return order;
    }
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
     * Message modifier which records but does not modify messages.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MessageRecorder
            implements MessageModifier
    {
        /* (non-Javadoc)
         * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
         */
        @Override
        public boolean modify(ServerFixSession inServerFixSession,
                              quickfix.Message inMessage)
        {
            synchronized(recordedMessages) {
                recordedMessages.addLast(inMessage);
                recordedMessages.notifyAll();
            }
            return false;
        }
        /**
         * Wait for and return the first recorded message (FIFO).
         *
         * @return a <code>quickfix.Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        private quickfix.Message getFirstRecordedMessage()
                throws Exception
        {
            return getFirstRecordedMessage(10);
        }
        /**
         * Wait for the given number of seconds for a message to be available and return it (FIFO).
         *
         * @param inSeconds an <code>int</code> value
         * @return a <code>quickfix.Message</code> value
         * @throws Exception if an unexpected error occurs
         */
        private quickfix.Message getFirstRecordedMessage(int inSeconds)
                throws Exception
        {
            long startTime = System.currentTimeMillis();
            long timeout = startTime + (inSeconds * 1000);
            quickfix.Message recordedMessage = null;
            while(recordedMessage == null && System.currentTimeMillis() < timeout) {
                synchronized(recordedMessages) {
                    recordedMessage = recordedMessages.pollFirst();
                }
                Thread.sleep(100);
            }
            assertNotNull("No recorded message in " + inSeconds + "s",
                          recordedMessage);
            return recordedMessage;
        }
        /**
         * Reset the recorded messages.
         */
        private void reset()
        {
            synchronized(recordedMessages) {
                recordedMessages.clear();
            }
        }
        /**
         * recorded messages
         */
        private final Deque<quickfix.Message> recordedMessages = Lists.newLinkedList();
    }
    /**
     * Test modifier which modifies the value of the text field for certain message types.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MockMessageModifier
            implements MessageModifier
    {
        /* (non-Javadoc)
         * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
         */
        @Override
        public boolean modify(ServerFixSession inServerFixSession,
                              quickfix.Message inMessage)
        {
            boolean modified = false;
            if(isModifyText()) {
                if(FIXMessageUtil.isOrderSingle(inMessage) || FIXMessageUtil.isExecutionReport(inMessage)) {
                    inMessage.setString(quickfix.field.Text.FIELD,
                                        textValue);
                    modified = true;
                }
            }
            if(isModifyUsername()) {
                if(FIXMessageUtil.isLogon(inMessage)) {
                    inMessage.setString(quickfix.field.Username.FIELD,
                                        usernameValue);
                    modified = true;
                }
            }
            if(isInterceptMessage()) {
                throw new MessageIntercepted();
            }
            return modified;
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
         * Get the modifyText value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isModifyText()
        {
            return modifyText;
        }
        /**
         * Sets the modifyText value.
         *
         * @param inModifyText a <code>boolean</code> value
         */
        private void setModifyText(boolean inModifyText)
        {
            modifyText = inModifyText;
        }
        /**
         * Get the modifyUsername value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isModifyUsername()
        {
            return modifyUsername;
        }
        /**
         * Sets the modifyUsername value.
         *
         * @param inModifyUsername a <code>boolean</code> value
         */
        private void setModifyUsername(boolean inModifyUsername)
        {
            modifyUsername = inModifyUsername;
        }
        /**
         * Get the usernameValue value.
         *
         * @return a <code>String</code> value
         */
        private String getUsernameValue()
        {
            return usernameValue;
        }
        /**
         * Get the interceptMessage value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isInterceptMessage()
        {
            return interceptMessage;
        }
        /**
         * Sets the interceptMessage value.
         *
         * @param inInterceptMessage a <code>boolean</code> value
         */
        private void setInterceptMessage(boolean inInterceptMessage)
        {
            interceptMessage = inInterceptMessage;
        }
        /**
         * indicate if the username field should be modified
         */
        private boolean modifyUsername = false;
        /**
         * indicate if the text field should be modified
         */
        private boolean modifyText = false;
        /**
         * indicates if the message should be intercepted
         */
        private boolean interceptMessage = false;
        /**
         * text value to apply to messages
         */
        private final String textValue = PlatformServices.generateId();
        /**
         * username value to apply to messages
         */
        private final String usernameValue = PlatformServices.generateId();
    }
    /**
     * provides access to the test session customization
     */
    @Autowired
    private TestSessionCustomization testSessionCustomization;
}
