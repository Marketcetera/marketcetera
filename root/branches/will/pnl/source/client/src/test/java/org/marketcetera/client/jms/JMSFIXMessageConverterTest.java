package org.marketcetera.client.jms;

import junit.framework.Test;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.field.Side;
import quickfix.Message;

import java.math.BigDecimal;

/**
 * Test the {@link JMSFIXMessageConverter} class.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class JMSFIXMessageConverterTest extends FIXVersionedTestCase {
    public JMSFIXMessageConverterTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(JMSFIXMessageConverterTest.class, FIXVersionTestSuite.ALL_VERSIONS);
    }

    public void testTextMessage() throws Exception {
        Message buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.34"), new BigDecimal("123"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ActiveMQTextMessage jmsMessage = new ActiveMQTextMessage();
        jmsMessage.setText(buy.toString());

        JMSFIXMessageConverter converter = new JMSFIXMessageConverter();

        assertEquals("mesasage not translated correctly", buy.toString(), converter.fromMessage(jmsMessage).toString()); //$NON-NLS-1$
    }

    public void testBytesMessage() throws Exception {
        Message buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.34"), new BigDecimal("123"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ActiveMQBytesMessage jmsMessage = new ActiveMQBytesMessage();
        jmsMessage.writeBytes(buy.toString().getBytes(JMSFIXMessageConverter.BYTES_MESSAGE_CHARSET));
        jmsMessage.reset();

        JMSFIXMessageConverter converter = new JMSFIXMessageConverter();
        assertEquals("mesasage not translated correctly", buy.toString(), converter.fromMessage(jmsMessage).toString()); //$NON-NLS-1$
    }
}
