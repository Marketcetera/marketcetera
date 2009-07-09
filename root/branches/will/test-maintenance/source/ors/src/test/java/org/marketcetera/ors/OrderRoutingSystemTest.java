package org.marketcetera.ors;

import junit.framework.Test;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.NullQuickFIXSender;
import org.marketcetera.ors.filters.MessageRouteManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import quickfix.Message;
import quickfix.field.OrderID;
import quickfix.field.Price;
import quickfix.field.Side;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.math.BigDecimal;

/**
 * Setup an ORS through Sring configuration.
 * Attach and additional sender on the JMS queue (outoingJMSTemplate) and a receiver on the topic (replyListener).
 * Send a buy order on the queue, and make sure that we have these:
 * 1. we get a confirmation execution report on the JMS topic
 * 2. we get an outgoing FIX message on the FIX listener
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@org.junit.Ignore
@ClassVersion("$Id$")
public class OrderRoutingSystemTest extends FIXVersionedTestCase
{
    private static ClassPathXmlApplicationContext appContext = null;
    private static JmsTemplate jmsQueueSender;
    private static NullQuickFIXSender qfSender;

    public OrderRoutingSystemTest(String inName, FIXVersion version)
    {
        super(inName, version);
    }

    public static Test suite()
    {
        return new FIXVersionTestSuite(OrderRoutingSystemTest.class,
                new FIXVersion[]{FIXVersion.FIX42});
    }


	protected void setUp() throws Exception {
        springSetup();
        super.setUp();
    }

    private static void springSetup() {
        //Initialize only once for the entire unit test
        if (appContext == null) {
            try {
                appContext = new ClassPathXmlApplicationContext(new String[]{
                        "message-modifiers.xml", //$NON-NLS-1$
                        "order-limits.xml", //$NON-NLS-1$
                        "ors-shared.xml",  //$NON-NLS-1$
                        "it-ors.xml", //$NON-NLS-1$
                        "ors_orm_vendor.xml", //$NON-NLS-1$
                        "ors_orm.xml", //$NON-NLS-1$
                        "ors_db.xml", //$NON-NLS-1$
                        "file:"+ApplicationBase.CONF_DIR+ //$NON-NLS-1$
                        "main.xml", //$NON-NLS-1$
                        "file:"+ApplicationBase.CONF_DIR+ //$NON-NLS-1$
                        "ors_base.xml" //$NON-NLS-1$
                    });
                jmsQueueSender = (JmsTemplate) appContext.getBean("outgoingJmsTemplate"); //$NON-NLS-1$
                qfSender = (NullQuickFIXSender) appContext.getBean("quickfixSender"); //$NON-NLS-1$
                // simulate logon
                QuickFIXApplication qfApp = (QuickFIXApplication) appContext.getBean("qfApplication"); //$NON-NLS-1$
                qfApp.onLogon(null);
            } catch (Exception e) {
                SLF4JLoggerProxy.error(OrderRoutingSystemTest.class.getName(), e, "Unable to initialize ORS"); //$NON-NLS-1$
                fail("Unable to init ORS: "+e.getMessage()); //$NON-NLS-1$
            }
        }
    }

    /** This is more of an integration test
     * Star the entire ORS, and then create an additional JMS topic queueSender (bound to
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
        MessageListenerAdapter recieveAdapter = (MessageListenerAdapter) appContext.getBean("replyListener"); //$NON-NLS-1$
        recieveAdapter.setDelegate(new TopicListener(topicMsgs));
        qfSender.setSemaphore(sema);

        // generate and send an order on JMS queue
        oneOrderRoundtripHelper(topicMsgs,  sema, Side.BUY);
        oneOrderRoundtripHelper(topicMsgs,  sema, Side.SELL);
        oneOrderRoundtripHelper(topicMsgs,  sema, Side.SELL_SHORT);
    }

    /** verify bug #361 - separateSuffix should be off by default */
    public void testSpringCreation() throws Exception {
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("/message-modifiers.xml"); //$NON-NLS-1$
        MessageRouteManager orm = (MessageRouteManager) appCtx.getBean("orderRouting", MessageRouteManager.class); //$NON-NLS-1$
        assertFalse(orm.isSeparateSuffix());
    }

    /** Generates a JMS message from the fix order
     *  Sends it on the jsm-commands queue
     * Verifies that an auto-generated report comes through the system
     */
    private void oneOrderRoundtripHelper(ArrayBlockingQueue<Message> topicMsgs,
                                         Semaphore sema, char inSide) throws Exception
    {
        SLF4JLoggerProxy.debug(this, "Before sending for {} sema is {}", fixDD.getHumanFieldValue(Side.FIELD, ""+inSide), sema.getQueueLength()); //$NON-NLS-1$ //$NON-NLS-2$

        // generate and send a buy order on JMS queue
        Message buyOrder = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("12.34"), new BigDecimal("32"), inSide, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        qfSender.getCapturedMessages().clear();
        topicMsgs.clear();
        jmsQueueSender.convertAndSend(buyOrder);

        // need to wait for both the JMS and FIX messages to come through
        sema.acquire();

        // verify we sent the FIX message through
        assertEquals("too many outgoing fix messages registered", 1, qfSender.getCapturedMessages().size()); //$NON-NLS-1$
        assertEquals(buyOrder.toString(), qfSender.getCapturedMessages().getFirst().toString());

        // verify we have 1 exec report
        Message execReport = topicMsgs.take();
        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        execReport.setField(new OrderID("fake-order-id")); //$NON-NLS-1$
        FIXMessageUtilTest.verifyExecutionReport(execReport, "32", "TOLI", inSide, msgFactory, fixDD); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("12.34", execReport.getString(Price.FIELD)); //$NON-NLS-1$
    }

}
