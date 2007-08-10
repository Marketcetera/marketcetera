package org.marketcetera.oms;

import junit.framework.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;
import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.*;

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
        QuickFIXApplication qfApp = new QuickFIXApplication(null);
        JmsOperations ops = new JmsTemplate() {

            public void convertAndSend(Object message) throws JmsException {
                throw new UncategorizedJmsException("testing exception handling: we always throw an exception");
            }
        };
        qfApp.setJmsOperations(ops);

        // these should not fail
        qfApp.fromAdmin(new Message(), new SessionID());
        qfApp.fromApp(new Message(), new SessionID());
    }

    public void testLogoutPropagated() throws Exception {
        QuickFIXApplication qfApp = new QuickFIXApplication(fixVersion.getMessageFactory());
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

    private class MockJmsTemplate extends JmsTemplate {
        private Vector<Message> sentMessages = new Vector<Message>();
        public void convertAndSend(Object message) throws JmsException {
            sentMessages.add((Message)message);
        }
    }
}
