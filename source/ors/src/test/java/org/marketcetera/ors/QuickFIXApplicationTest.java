package org.marketcetera.ors;

import junit.framework.Test;
import org.marketcetera.core.*;
import org.marketcetera.ors.MessageModifierManager;
import org.marketcetera.ors.ORSMessageKey;
import org.marketcetera.ors.OrderRoutingSystem;
import org.marketcetera.ors.QuickFIXApplication;
import org.marketcetera.quickfix.*;
import org.marketcetera.spring.MockJmsTemplate;
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
        return new FIXVersionTestSuite(QuickFIXApplicationTest.class, OrderRoutingSystem.ORS_MESSAGE_BUNDLE_INFO,
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
        qfApp.fromAdmin(new Message(), new SessionID("begin", "sender", "target"));
        Message execReport = msgFactory.newExecutionReport("123", "456", "789", OrdStatus.FILLED, Side.BUY, new BigDecimal(100), new BigDecimal("10.10"),
                new BigDecimal(100), new BigDecimal("10.10"), new BigDecimal(100), new BigDecimal("10.10"), new MSymbol("XYZ"), "bob");
        qfApp.fromApp(execReport, new SessionID("begin", "sender", "target"));
    }

    public void testLogoutPropagated() throws Exception {
        QuickFIXApplication qfApp = new MockQuickFIXApplication(fixVersion.getMessageFactory());
        MockJmsTemplate jmsTemplate = new MockJmsTemplate();
        qfApp.setJmsOperations(jmsTemplate);

        qfApp.onLogout(new SessionID(FIXVersion.FIX42.toString(), "sender", "target"));
        assertEquals(1, jmsTemplate.getSentMessages().size());
        Message received = jmsTemplate.getSentMessages().get(0);
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

        assertEquals(1, jmsTemplate.getSentMessages().size());

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

        assertEquals(0, jmsTemplate.getSentMessages().size());
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
        assertEquals(1, jmsTemplate.getSentMessages().size());
        assertEquals(1, tradeRecorderJMS.getSentMessages().size());
        jmsTemplate.getSentMessages().clear();
        tradeRecorderJMS.getSentMessages().clear();

        // now set JMS ops to null, but trade recorder should still get a message
        qfApp.setJmsOperations(null);
        qfApp.fromApp(msg, new SessionID(fixVersion.toString(), "sender", "target"));
        assertEquals(0, jmsTemplate.getSentMessages().size());
        assertEquals(1, tradeRecorderJMS.getSentMessages().size());
    }

    /** Test that when underlying QFJ engine sends out an outgoing reject
     * we intercept it and put more data into it and send it out to JMS topic
     */
    public void testOutgoingRejectCopiedToJMS() throws Exception {
        final QuickFIXApplication qfApp = new QuickFIXApplication(fixVersion.getMessageFactory());
        MockJmsTemplate jmsTemplate = new MockJmsTemplate();
        qfApp.setJmsOperations(jmsTemplate);
        SessionID sessionID = new SessionID(fixVersion.toString(), "sender", "target");

        Message logon = msgFactory.createMessage(MsgType.LOGON);
        qfApp.toAdmin(logon, sessionID);

        assertEquals("Logon shouldn't have been copied to JSM", 0, jmsTemplate.getSentMessages().size());
        assertFalse("Shouldn't have had Text tag in outgoing logon", logon.isSetField(Text.FIELD));
        jmsTemplate.getSentMessages().clear();

        // now send a reject w/out RefMsgType field - should not be modified
        Message reject = msgFactory.createMessage(MsgType.REJECT);
        reject.setField(new Text("no refmsgtype"));
        qfApp.toAdmin(reject, sessionID);
        assertEquals("text field should not be modified", "no refmsgtype", reject.getString(Text.FIELD));
        assertEquals("Reject should've been copied to JMS", 1, jmsTemplate.getSentMessages().size());
        jmsTemplate.getSentMessages().clear();

        // now add real fields
        reject.setField(new RefMsgType(MsgType.EXECUTION_REPORT));
        reject.setField(new Text("Invalid tag number"));

        // send it through
        qfApp.toAdmin(reject, sessionID);
        String expectedErr = ORSMessageKey.ERROR_INCOMING_MSG_REJECTED.getLocalizedMessage(
                fixDD.getHumanFieldValue(MsgType.FIELD, MsgType.EXECUTION_REPORT), "Invalid tag number");
        assertEquals(expectedErr, reject.getString(Text.FIELD));
        assertEquals("Reject should've been copied to JMS", 1, jmsTemplate.getSentMessages().size());
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
