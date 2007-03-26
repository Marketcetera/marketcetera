package org.marketcetera.oms;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import junit.framework.Test;
import junit.framework.TestCase;
import quickfix.Message;
import quickfix.SessionID;

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
public class QuickFIXApplicationTest extends TestCase {
    public QuickFIXApplicationTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(QuickFIXApplicationTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO);
    }


    public void testMessageSendWhenJMSBarfs() throws Exception {
        QuickFIXApplication qfApp = new QuickFIXApplication();
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
}
