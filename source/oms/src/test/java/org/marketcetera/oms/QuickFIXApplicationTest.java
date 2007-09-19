package org.marketcetera.oms;

import junit.framework.Test;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.quickfix.NullQuickFIXSender;
import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import quickfix.JdbcLogFactory;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.*;

import java.math.BigDecimal;
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
        QuickFIXApplication qfApp = new MockQuickFIXApplication(null, null);
        JmsOperations ops = new JmsTemplate() {
            public void convertAndSend(Object message) throws JmsException {
                throw new UncategorizedJmsException("testing exception handling: we always throw an exception");
            }
        };
        qfApp.setJmsOperations(ops);

        // these should not fail
        qfApp.fromAdmin(new Message(), new SessionID());
        qfApp.fromApp(msgFactory.createMessage(MsgType.EXECUTION_REPORT), new SessionID());
    }

    public void testLogoutPropagated() throws Exception {
        QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory(), null);
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
        QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory(), null);
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
        final QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory(), null);
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

    private class MockJmsTemplate extends JmsTemplate {
        private Vector<Message> sentMessages = new Vector<Message>();
        public void convertAndSend(Object message) throws JmsException {
            sentMessages.add((Message)message);
        }
    }

    public static class MockQuickFIXApplication extends QuickFIXApplication {
        public MockQuickFIXApplication(FIXMessageFactory fixMessageFactory, JdbcLogFactory logFactory) {
            super(fixMessageFactory, logFactory);
        }

        protected void logMessage(Message message, SessionID sessionID) {
            // noop
        }

        protected IQuickFIXSender createQuickFIXSender() {
            return new NullQuickFIXSender();
        }
    }
}
