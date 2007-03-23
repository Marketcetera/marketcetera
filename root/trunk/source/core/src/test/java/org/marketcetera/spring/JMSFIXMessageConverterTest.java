package org.marketcetera.spring;

import junit.framework.Test;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.field.Side;

/**
 * Test the {@link JMSFIXMessageConverter} class.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class JMSFIXMessageConverterTest extends FIXVersionedTestCase {
    public JMSFIXMessageConverterTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(JMSFIXMessageConverterTest.class, FIXVersionTestSuite.ALL_VERSIONS);
    }

    public void testTextMessage() throws Exception {
        quickfix.Message buy = FIXMessageUtilTest.createNOS("TOLI", 23.34, 123, Side.BUY, msgFactory);
        ActiveMQTextMessage jmsMessage = new ActiveMQTextMessage();
        jmsMessage.setText(buy.toString());

        JMSFIXMessageConverter converter = new JMSFIXMessageConverter();

        assertEquals("mesasage not translated correctly", buy.toString(), converter.fromMessage(jmsMessage).toString());
    }

    public void testBytesMessage() throws Exception {
        quickfix.Message buy = FIXMessageUtilTest.createNOS("TOLI", 23.34, 123, Side.BUY, msgFactory);
        ActiveMQBytesMessage jmsMessage = new ActiveMQBytesMessage();
        jmsMessage.writeBytes(buy.toString().getBytes(JMSFIXMessageConverter.BYTES_MESSAGE_CHARSET));
        jmsMessage.reset();

        JMSFIXMessageConverter converter = new JMSFIXMessageConverter();
        assertEquals("mesasage not translated correctly", buy.toString(), converter.fromMessage(jmsMessage).toString());
    }
}
