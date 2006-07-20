package org.marketcetera.oms;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.ConfigData;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.jcyclone.JMSOutputInfo;
import org.marketcetera.jms.JMSAdapter;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.quickfix.FIXMessageUtil;
import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

import javax.jms.MessageProducer;
import java.math.BigDecimal;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
public class EventSender {

    public static void main(String[] args) throws Exception
    {
        Message msg = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100),
                new MSymbol("IBM"), TimeInForce.DAY, new AccountID("bobAccount"));

        OrderManagementSystem oms = new OrderManagementSystemIT.MyOMS("oms");
        oms.init();

        ConfigData props = oms.getInitProps();
        String queueName = props.get(ConnectionConstants.JMS_INCOMING_QUEUE_KEY, "");
        String connectionFactory = props.get(ConnectionConstants.JMS_CONNECTION_FACTORY_KEY, "");
        String initialContextFactory = props.get(ConnectionConstants.JMS_CONTEXT_FACTORY_KEY, "");
        String url = props.get(ConnectionConstants.JMS_URL_KEY, "");

        JMSAdapter jmsAdapter = new JMSAdapter(initialContextFactory, url, connectionFactory, true);
        jmsAdapter.connectOutgoingQueue(JMSAdapterSource.INCOMING_QUEUE_NAME, queueName,
                javax.jms.Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = jmsAdapter.getOutgoingQueueSender(JMSAdapterSource.INCOMING_QUEUE_NAME);
        javax.jms.Session session = jmsAdapter.getOutgoingQueueSession(JMSAdapterSource.INCOMING_QUEUE_NAME);
        oms.registerOutgoingJMSInfo(new JMSOutputInfo(producer, session, queueName));


        javax.jms.Message jmsMessage = session.createTextMessage(msg.toString());
        producer.send(jmsMessage);

        // now send a message on a topic
        String topicName = props.get(ConnectionConstants.JMS_OUTGOING_TOPIC_KEY, "");
        jmsAdapter.connectOutgoingTopic(JMSAdapterSource.OUTGOING_TOPIC_NAME, topicName,
                javax.jms.Session.AUTO_ACKNOWLEDGE);
        MessageProducer topicProduer = jmsAdapter.getOutgoingTopicPublisher(JMSAdapterSource.OUTGOING_TOPIC_NAME);
        topicProduer.send(jmsMessage);


        jmsAdapter.shutdown();
    }
}
