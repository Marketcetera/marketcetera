package org.marketcetera.oms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.extensions.RepeatedTest;
import org.jcyclone.core.cfg.MapConfig;
import org.marketcetera.core.*;
import org.marketcetera.jcyclone.JMSOutputInfo;
import org.marketcetera.jms.JMSAdapter;
import org.marketcetera.jms.JMSAdapterTest;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.OrderModifierFactory;
import quickfix.Message;
import quickfix.field.Price;
import quickfix.field.Side;

import javax.jms.*;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Semaphore;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderManagementSystemIT extends TestCase
{
    public static final String CONFIG_FILE = "oms-test";
    private static MyOMS sOMS;

    public OrderManagementSystemIT(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
        try {
            sOMS = new MyOMS(CONFIG_FILE);
            sOMS.init();
            sOMS.run();
        } catch (Exception e) {
            LoggerAdapter.error("Unable to initialize OMS", e, OrderManagementSystemIT.class.getName());
            fail("Unable to init OMS");
        }

        TestSuite suite = new  MarketceteraTestSuite(OrderManagementSystemIT.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO);
        suite.addTest(new RepeatedTest(new OrderManagementSystemIT("testEndToEndOrderFilling"), 5));
        return suite;
    }

    public void testVerifyConfigReadCorrectly() throws Exception
    {
        ConfigData props = sOMS.getInitProps();
        assertNotNull(props);
        assertEquals(23, props.keys().length);
        // check att least one went through
        assertEquals(OrderManagerTest.HEADER_57_VAL,
                props.get(OrderModifierFactory.FIX_HEADER_PREFIX+"57", ""));
    }

    /** This is more of an integration test
     * Star the entire OMS, and then create an additional JMS topic adapter (bound to
     * output on the queue and read/write on the JMS topic).
     *
     * Send a BUY on the order queue, and then make sure we get the auto-generated ExecReport
     * on the topic.
     *
     *
     */
    public void testEndToEndOrderFilling() throws Exception
    {
        // connect to OMS commands queue
        Properties props = sOMS.getProperties();
        String qName = props.getProperty(ConnectionConstants.JMS_INCOMING_QUEUE_KEY);
        String topicName = props.getProperty(ConnectionConstants.JMS_OUTGOING_TOPIC_KEY);
        JMSAdapter adapter = new JMSAdapter(JMSAdapterTest.CONTEXT_FACTORY_NAME,JMSAdapterTest.PROVIDER_URL,
                JMSAdapterTest.CONNECTION_FACTORY_NAME, true);
        adapter.connectOutgoingQueue(JMSAdapterSource.INCOMING_QUEUE_NAME, qName, Session.AUTO_ACKNOWLEDGE);
        // need to connect both the incoming and outgoing topic subscribers since we both send and read on that topic
        adapter.connectOutgoingTopic(JMSAdapterSource.OUTGOING_TOPIC_NAME, topicName, Session.AUTO_ACKNOWLEDGE);
        adapter.connectIncomingTopic(JMSAdapterSource.OUTGOING_TOPIC_NAME, topicName, Session.AUTO_ACKNOWLEDGE);
        Session queueSession = adapter.getOutgoingQueueSession(JMSAdapterSource.INCOMING_QUEUE_NAME);
        MessageProducer producer = adapter.getOutgoingTopicPublisher(JMSAdapterSource.OUTGOING_TOPIC_NAME);
        sOMS.registerOutgoingJMSInfo(new JMSOutputInfo(producer, queueSession, topicName));
        adapter.start();


        // now listen for the auto-generate execution report
        final Semaphore sema = new Semaphore(0);
        final Vector<Message> topicMsgs = new Vector<Message>();
        TopicSubscriber incomingTopicSubscriber = adapter.getIncomingTopicSubscriber(JMSAdapterSource.OUTGOING_TOPIC_NAME);
        incomingTopicSubscriber.setMessageListener(new MessageListener() {
            public void onMessage(javax.jms.Message message) {
                try {
                    Message theMsg = new Message(((TextMessage) message).getText());
                    topicMsgs.add(theMsg);
                    sema.release();
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        String humanSide = FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD,
                                ""+theMsg.getChar(Side.FIELD));
                        LoggerAdapter.debug("Released sema "+sema.getQueueLength() + " for side "+humanSide, this);
                    }
                } catch (Exception ex) {
                    LoggerAdapter.error("Failed in message receive", ex, this);
                    fail("failed in message receive");
                }
            }
        });

        // generate and send an order on JMS queue
        oneOrderRoundtripHelper(adapter, topicMsgs,  sema, Side.BUY);
        oneOrderRoundtripHelper(adapter, topicMsgs,  sema, Side.SELL);
        oneOrderRoundtripHelper(adapter, topicMsgs,  sema, Side.SELL_SHORT);
    }

    /** Generates a JMS message from the fix order
     *  Sends it on the jsm-commands queue
     * Verifies that an auto-generated report comes through the system
     */
    private void oneOrderRoundtripHelper(JMSAdapter adapter, Vector<Message> topicMsgs,
                                         Semaphore sema, char inSide) throws Exception
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Before sending for "+FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD, ""+inSide) +
            " sema is "+sema.getQueueLength(), this);
        }

        // generate and send a buy order on JMS queue
        Message buyOrder = FIXMessageUtilTest.createNOS("TOLI", 12.34, 32, inSide);
        javax.jms.Message jmsMessage = adapter.getOutgoingQueueSession(JMSAdapterSource.INCOMING_QUEUE_NAME).createTextMessage(buyOrder.toString());
        adapter.getOutgoingQueueSender(JMSAdapterSource.INCOMING_QUEUE_NAME).send(jmsMessage);

        sema.acquire();

        // verify we have 1 exec report
        assertEquals("testing side "+ FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD, ""+inSide),
                        1, topicMsgs.size());
        Message execReport = topicMsgs.get(0);
        FIXMessageUtilTest.verifyExecutionReport(execReport, "32", "TOLI", inSide);
        assertEquals("12.34", execReport.getString(Price.FIELD));

        topicMsgs.clear();
    }

    /** Dummy implementation of the OMS that returns an empty JMSOutputInfo */
    public static class MyOMS extends OrderManagementSystem
    {
        protected MyOMS(String inCfgFile) throws ConfigFileLoadingException
        {
            super(inCfgFile);
            sOMS = this;
        }
    }
}
