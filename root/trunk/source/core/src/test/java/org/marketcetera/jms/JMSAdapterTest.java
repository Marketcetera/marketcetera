package org.marketcetera.jms;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;

import javax.jms.*;
import java.util.concurrent.Semaphore;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("Id")
public class JMSAdapterTest extends TestCase {

    public static final String CONTEXT_FACTORY_NAME = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    // don't use an external MQ - use the in-process one ActiveMQ supports
    public static final String PROVIDER_URL = "peer://junit/test";
    public static final String CONNECTION_FACTORY_NAME = "ConnectionFactory";

    public JMSAdapterTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(JMSAdapterTest.class);
    }

    public void testJMSAdapter() throws Exception {
        String topicName = "jms-test-"+System.currentTimeMillis();
        JMSAdapter adapter = new JMSAdapter(CONTEXT_FACTORY_NAME,PROVIDER_URL,CONNECTION_FACTORY_NAME, true);
        adapter.connectOutgoingTopic("outgoing_topic", topicName, Session.AUTO_ACKNOWLEDGE);
        adapter.connectIncomingTopic("incoming_topic", topicName, Session.AUTO_ACKNOWLEDGE);
        adapter.start();

        final Semaphore sema = new Semaphore(0);
        TopicSubscriber incomingTopicSubscriber = adapter.getIncomingTopicSubscriber("incoming_topic");
        incomingTopicSubscriber.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                System.out.println("Got here");
                sema.release();
            }
        });

        TextMessage textMessage = adapter.getOutgoingTopicSession("outgoing_topic").createTextMessage("foo");
        adapter.getOutgoingTopicPublisher("outgoing_topic").send(textMessage);
        sema.acquire();
    }

}
