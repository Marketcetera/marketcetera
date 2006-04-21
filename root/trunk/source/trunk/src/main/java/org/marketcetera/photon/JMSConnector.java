package org.marketcetera.photon;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.marketcetera.jms.JMSAdapter;

public class JMSConnector {

	
	private static final String CONTEXT_FACTORY_NAME = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	private static final String PROVIDER_URL = "tcp://server01:61616";
	private static final String CONNECTION_FACTORY_NAME = "ConnectionFactory";

	private static final String INCOMING_TOPIC_NAME = "oms-messages";
	private static final String OUTGOING_QUEUE_NAME = "oms-commands";
	
	private static JMSConnector sInstance;
	JMSAdapter adapter;
	
	private JMSConnector()
	{
		
	}

	public static void init() throws JMSException
	{
		sInstance = new JMSConnector();
		sInstance.adapter = new JMSAdapter(
				CONTEXT_FACTORY_NAME,
				PROVIDER_URL,
				CONNECTION_FACTORY_NAME);
		sInstance.adapter.start();
		sInstance.adapter.connectIncomingTopic(INCOMING_TOPIC_NAME, INCOMING_TOPIC_NAME, javax.jms.Session.AUTO_ACKNOWLEDGE);
		sInstance.adapter.connectOutgoingQueue(OUTGOING_QUEUE_NAME, OUTGOING_QUEUE_NAME, javax.jms.Session.AUTO_ACKNOWLEDGE);
	}
	
	public static JMSConnector getInstance()
	{
		return sInstance;
	}
	
	public void sendToQueue(javax.jms.Message aMessage) throws JMSException {
		adapter.getOutgoingQueueSender(OUTGOING_QUEUE_NAME).send(aMessage);
	}
	
	public void sendToQueue(quickfix.Message aMessage) throws JMSException{
		try {
			TextMessage textMessage = adapter.getOutgoingQueueSession(OUTGOING_QUEUE_NAME).createTextMessage();
			textMessage.setText(aMessage.toString());
			sendToQueue(textMessage);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	public void setTopicListener(MessageListener listener) throws JMSException{
		adapter.getIncomingTopicSubscriber(INCOMING_TOPIC_NAME).setMessageListener(listener);
	}
}
