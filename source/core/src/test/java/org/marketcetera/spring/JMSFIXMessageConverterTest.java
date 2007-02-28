package org.marketcetera.spring;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import junit.framework.Test;
import junit.framework.TestCase;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Session;

import quickfix.field.Side;

/**
 * Test the {@link JMSFIXMessageConverter} class.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class JMSFIXMessageConverterTest extends TestCase {
    public JMSFIXMessageConverterTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(JMSFIXMessageConverterTest.class);
    }

    public void testTextMessage() throws Exception {
        quickfix.Message buy = FIXMessageUtilTest.createNOS("TOLI", 23.34, 123, Side.BUY);
        ActiveMQTextMessage jmsMessage = new ActiveMQTextMessage();
        jmsMessage.setText(buy.toString());

        JMSFIXMessageConverter converter = new JMSFIXMessageConverter();

        assertEquals("mesasage not translated correctly", buy.toString(), converter.fromMessage(jmsMessage).toString());
    }

    public void testBytesMessage() throws Exception {
        quickfix.Message buy = FIXMessageUtilTest.createNOS("TOLI", 23.34, 123, Side.BUY);
        ActiveMQBytesMessage jmsMessage = new ActiveMQBytesMessage();
        jmsMessage.writeBytes(buy.toString().getBytes(JMSFIXMessageConverter.BYTES_MESSAGE_CHARSET));
        jmsMessage.reset();

        JMSFIXMessageConverter converter = new JMSFIXMessageConverter();
        assertEquals("mesasage not translated correctly", buy.toString(), converter.fromMessage(jmsMessage).toString());
    }
}
