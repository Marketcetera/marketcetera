package org.marketcetera.oms;

import junit.framework.Test;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import quickfix.Message;
import quickfix.field.OrderID;
import quickfix.field.Price;
import quickfix.field.Side;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Setup an OMS through Sring configuration.
 * Attach and additional sender on the JMS queue (outoingJMSTemplate) and a receiver on the topic (replyListener).
 * Send a buy order on the queue, and make sure that we have these:
 * 1. we get a confirmation execution report on the JMS topic
 * 2. we get an outgoing FIX message on the FIX listener
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderManagementSystemTest extends FIXVersionedTestCase
{
    private static ClassPathXmlApplicationContext appContext;
    private static JmsTemplate jmsQueueSender;
    private static NullQuickFIXSender qfSender;

    public OrderManagementSystemTest(String inName, FIXVersion version)
    {
        super(inName, version);
    }

    public static Test suite()
    {
        return new FIXVersionTestSuite(OrderManagementSystemTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO,
                new FIXVersion[]{FIXVersion.FIX42});
    }


	protected void setUp() throws Exception {
        super.setUp();
        try {
        	appContext = new ClassPathXmlApplicationContext(new String[]{"order-modifiers.xml", "order-limits.xml",
                    "oms-shared.xml", "it-oms.xml"});
			jmsQueueSender = (JmsTemplate) appContext.getBean("outgoingJmsTemplate");
            qfSender = (NullQuickFIXSender) appContext.getBean("quickfixSender");
            // simulate logon
            QuickFIXApplication qfApp = (QuickFIXApplication) appContext.getBean("qfApplication");
            qfApp.onLogon(null);
        } catch (Exception e) {
            LoggerAdapter.error("Unable to initialize OMS", e, OrderManagementSystemTest.class.getName());
            fail("Unable to init OMS: "+e.getMessage());
        }
	}

	/** This is more of an integration test
     * Star the entire OMS, and then create an additional JMS topic queueSender (bound to
     * output on the queue and read/write on the JMS topic).
     *
     * Send a BUY on the order queue, and then make sure we get the auto-generated ExecReport
     * on the topic.
     *
     *
     */
    public void testEndToEndOrderFilling() throws Exception
    {
    	// now listen for the auto-generate execution report
        final Semaphore sema = new Semaphore(0);
        final ArrayBlockingQueue<Message> topicMsgs = new ArrayBlockingQueue<Message>(1);
        MessageListenerAdapter recieveAdapter = (MessageListenerAdapter) appContext.getBean("replyListener");
        recieveAdapter.setDelegate(new TopicListener(topicMsgs));
        qfSender.setSemaphore(sema);

        // generate and send an order on JMS queue
        oneOrderRoundtripHelper(topicMsgs,  sema, Side.BUY);
        oneOrderRoundtripHelper(topicMsgs,  sema, Side.SELL);
        oneOrderRoundtripHelper(topicMsgs,  sema, Side.SELL_SHORT);
    }

    /** Generates a JMS message from the fix order
     *  Sends it on the jsm-commands queue
     * Verifies that an auto-generated report comes through the system
     */
    private void oneOrderRoundtripHelper(ArrayBlockingQueue<Message> topicMsgs,
                                         Semaphore sema, char inSide) throws Exception
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Before sending for "+ fixDD.getHumanFieldValue(Side.FIELD, ""+inSide) +
            " sema is "+sema.getQueueLength(), this);
        }

        // generate and send a buy order on JMS queue
        Message buyOrder = FIXMessageUtilTest.createNOS("TOLI", 12.34, 32, inSide, msgFactory);
        qfSender.getCapturedMessages().clear();
        topicMsgs.clear();
        jmsQueueSender.convertAndSend(buyOrder);

        // need to wait for both the JMS and FIX messages to come through
        sema.acquire();

        // verify we sent the FIX message through
        assertEquals("too many outgoing fix messages registered", 1, qfSender.getCapturedMessages().size());
        assertEquals(buyOrder.toString(), qfSender.getCapturedMessages().getFirst().toString());

        // verify we have 1 exec report
        Message execReport = topicMsgs.take();
        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        execReport.setField(new OrderID("fake-order-id"));
        FIXMessageUtilTest.verifyExecutionReport(execReport, "32", "TOLI", inSide, msgFactory, fixDD);
        assertEquals("12.34", execReport.getString(Price.FIELD));
    }

}
