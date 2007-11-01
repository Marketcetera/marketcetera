package org.marketcetera.oms;

import junit.framework.Test;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.*;
import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Vector;

/**
 * Verifies that we don't error out of the sending functions
 * even if the underlying JMS connection goes stale
 * Otherwise we end up logging out of the FIX connection as well
 * This is in reference to ticket 120
 * 
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class QuickFIXApplicationTest extends FIXVersionedTestCase {
    public QuickFIXApplicationTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(QuickFIXApplicationTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO,
                FIXVersionTestSuite.ALL_VERSIONS);
    }


    public void testMessageSendWhenJMSBarfs() throws Exception {
        QuickFIXApplication qfApp = new MockQuickFIXApplication(null);
        JmsOperations ops = new JmsTemplate() {
            public void convertAndSend(Object message) throws JmsException {
                throw new UncategorizedJmsException("testing exception handling: we always throw an exception");
            }
        };
        qfApp.setJmsOperations(ops);

        // these should not fail
        qfApp.fromAdmin(new Message(), new SessionID());
        Message execReport = msgFactory.newExecutionReport("123", "456", "789", OrdStatus.FILLED, Side.BUY, new BigDecimal(100), new BigDecimal("10.10"),
                new BigDecimal(100), new BigDecimal("10.10"), new BigDecimal(100), new BigDecimal("10.10"), new MSymbol("XYZ"), "bob");
        qfApp.fromApp(execReport, new SessionID());
    }

    public void testLogoutPropagated() throws Exception {
        QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory());
        MockJmsTemplate jmsTemplate = new MockJmsTemplate();
        qfApp.setJmsOperations(jmsTemplate);

        qfApp.onLogout(new SessionID(FIXVersion.FIX42.toString(), "sender", "target"));
        assertEquals(1, jmsTemplate.sentMessages.size());
        Message received = jmsTemplate.sentMessages.get(0);
        assertEquals(MsgType.LOGOUT, received.getHeader().getString(MsgType.FIELD));
        assertEquals("sender", received.getHeader().getString(SenderCompID.FIELD));
        assertEquals("target", received.getHeader().getString(TargetCompID.FIELD));
        assertNotNull(received.getHeader().getString(SendingTime.FIELD));
    }

    /** This is a test for OpenFix certification. Our app should reject everything
     * that has a DeliverToCompID present in it
     */
    public void testWithDeliverToCompID() throws Exception {
        QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory());
        MockJmsTemplate jmsTemplate = new MockJmsTemplate();
        qfApp.setJmsOperations(jmsTemplate);

        Message msg = msgFactory.newExecutionReport("200", "300", "400", OrdStatus.CANCELED, Side.BUY, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("BOB"), "account");
        msg.getHeader().setField(new MsgSeqNum(1000));
        msg.getHeader().setField(new SenderCompID("sender"));
        msg.getHeader().setField(new TargetCompID("target"));
        msg.getHeader().setField(new DeliverToCompID("bob"));
        SessionID session = new SessionID(FIXVersion.FIX42.toString(), "sender", "target");
        qfApp.fromApp(msg, session);

        assertEquals(1, jmsTemplate.sentMessages.size());

        assertEquals(1, ((NullQuickFIXSender) qfApp.quickFIXSender).getCapturedMessages().size());
        Message reject = ((NullQuickFIXSender) qfApp.quickFIXSender).getCapturedMessages().get(0);
        assertEquals(MsgType.REJECT, reject.getHeader().getString(MsgType.FIELD));
        assertEquals(SessionRejectReason.COMPID_PROBLEM, reject.getInt(SessionRejectReason.FIELD));
        assertEquals(1000, reject.getInt(RefSeqNum.FIELD));
        assertEquals(MsgType.EXECUTION_REPORT, reject.getString(RefMsgType.FIELD));
        assertTrue(reject.getString(Text.FIELD), reject.getString(Text.FIELD).contains("bob"));
     }

    public void testUnsupportedMessageType_AllocationAck() throws Exception {
        final QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory());
        MockJmsTemplate jmsTemplate = new MockJmsTemplate();
        qfApp.setJmsOperations(jmsTemplate);

        final Message ack = msgFactory.createMessage(MsgType.ALLOCATION_INSTRUCTION_ACK);
        new ExpectedTestFailure(UnsupportedMessageType.class) {
            protected void execute() throws Throwable {
                qfApp.fromApp(ack, new SessionID(FIXVersion.FIX42.toString(), "sender", "target"));
            }
        }.run();

        assertEquals(0, jmsTemplate.sentMessages.size());
    }

    public void testMessageModifiersAppliedToOutgoingAdminMessages() throws Exception {
        final QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory());
        DefaultMessageModifier modifier = new DefaultMessageModifier();
        modifier.setHeaderFields(DefaultMessageModifierTest.createFieldsMap(new String[][] {{"50(A)", "headerValue"}}));
        modifier.setMsgFields(DefaultMessageModifierTest.createFieldsMap(new String[][] {{"37(A)", "messageValue"}}));
        qfApp.setMessageModifierMgr(new MessageModifierManager(Arrays.asList((MessageModifier) modifier), msgFactory));
        Message msg = msgFactory.createMessage(MsgType.LOGON);
        qfApp.toAdmin(msg, new SessionID(fixVersion.toString(), "sender", "target"));
        assertEquals("field 37 not present in message", "messageValue", msg.getString(37));
        assertEquals("field 50 not present in header", "headerValue", msg.getHeader().getString(50));
    }

    public void testExecutionReportGoesToTradeTopic() throws Exception {
        final QuickFIXApplication qfApp = new QuickFIXApplication(fixVersion.getMessageFactory());
        MockJmsTemplate jmsTemplate = new MockJmsTemplate();
        qfApp.setJmsOperations(jmsTemplate);
        MockJmsTemplate tradeRecorderJMS = new MockJmsTemplate();
        qfApp.setTradeRecorderJMS(tradeRecorderJMS);

        Message msg = msgFactory.newExecutionReport("123", "456", "789", OrdStatus.FILLED, Side.BUY, new BigDecimal(100), new BigDecimal("10.10"),
                new BigDecimal(100), new BigDecimal("10.10"), new BigDecimal(100), new BigDecimal("10.10"), new MSymbol("XYZ"), "bob");

        qfApp.fromApp(msg, new SessionID(fixVersion.toString(), "sender", "target"));
        assertEquals(1, jmsTemplate.sentMessages.size());
        assertEquals(1, tradeRecorderJMS.sentMessages.size());
        jmsTemplate.sentMessages.clear();
        tradeRecorderJMS.sentMessages.clear();

        // now set JMS ops to null, but trade recorder should still get a message
        qfApp.setJmsOperations(null);
        qfApp.fromApp(msg, new SessionID(fixVersion.toString(), "sender", "target"));
        assertEquals(0, jmsTemplate.sentMessages.size());        
        assertEquals(1, tradeRecorderJMS.sentMessages.size());
    }

    private class MockJmsTemplate extends JmsTemplate {
        private Vector<Message> sentMessages = new Vector<Message>();
        public void convertAndSend(Object message) throws JmsException {
            sentMessages.add((Message)message);
        }
    }

    public static class MockQuickFIXApplication extends QuickFIXApplication {
        public MockQuickFIXApplication(FIXMessageFactory fixMessageFactory) {
            super(fixMessageFactory);
        }

        protected void logMessage(Message message, SessionID sessionID) {
            // noop
        }

        protected IQuickFIXSender createQuickFIXSender() {
            return new NullQuickFIXSender();
        }
    }
}
