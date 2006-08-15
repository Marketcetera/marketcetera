package org.marketcetera.photon;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.eclipse.jface.util.ListenerList;
import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.jms.JMSAdapter;

public class JMSConnector implements FeedComponent {

	public static final String JMS_CONNECTOR_ID = "Message Queue";

	private JMSAdapter adapter;

	private String outgoingQueueName;

	private String incomingTopicName;

	private FeedStatus feedStatus = FeedStatus.UNKNOWN;

	private ListenerList listeners = new ListenerList();

	JMSConnector() {
	}

	public void init(String incomingTopicName, String outgoingQueueName,
			String contextFactoryName, String providerUrl,
			String connectionFactoryName) throws JMSException {
		try {
			adapter = new JMSAdapter(contextFactoryName, providerUrl,
					connectionFactoryName);
			adapter.start();
			adapter.connectIncomingTopic(incomingTopicName, incomingTopicName,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			adapter.connectOutgoingQueue(outgoingQueueName, outgoingQueueName,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			this.outgoingQueueName = outgoingQueueName;
			this.incomingTopicName = incomingTopicName;
			setStatus(FeedStatus.AVAILABLE);
		} catch (JMSException e) {
			setStatus(FeedStatus.ERROR);
			throw e;
		}
	}
	
	public void shutdown(){
		try {
			if (adapter != null){
				adapter.shutdown();
				setStatus(FeedStatus.OFFLINE);
			}
		} finally {
			adapter = null;
		}
	}

	public void sendToQueue(javax.jms.Message aMessage) throws JMSException {
		// TODO: check feed status before sending?
		adapter.getOutgoingQueueSender(outgoingQueueName).send(aMessage);
	}

	public void sendToQueue(quickfix.Message aMessage) throws JMSException {
		// TODO: check feed status before sending?
		try {
			TextMessage textMessage = adapter.getOutgoingQueueSession(
					outgoingQueueName).createTextMessage();
			textMessage.setText(aMessage.toString());
			sendToQueue(textMessage);
		} catch (JMSException e) {
			setStatus(FeedStatus.ERROR);
			throw e;
		}
	}

	public void setTopicListener(MessageListener listener) throws JMSException {
		try {
			adapter.getIncomingTopicSubscriber(incomingTopicName)
					.setMessageListener(listener);
		} catch (JMSException e) {
			setStatus(FeedStatus.ERROR);
			throw e;
		}
	}
	
	private void setStatus(FeedStatus theStatus){
		feedStatus = theStatus;
		fireFeedComponentChanged();
	}

	public FeedType getFeedType() {
		return FeedType.SIMULATED;
	}

	public FeedStatus getFeedStatus() {
		return feedStatus;
	}

	public String getID() {
		// TODO Auto-generated method stub
		return JMS_CONNECTOR_ID;
	}

	public void addFeedComponentListener(IFeedComponentListener arg0) {
		listeners.add(arg0);
	}

	public void removeFeedComponentListener(IFeedComponentListener arg0) {
		listeners.remove(arg0);
	}
	
	private void fireFeedComponentChanged()
	{
		for (Object aListenerObject : listeners.getListeners()) {
			IFeedComponentListener aListener = (IFeedComponentListener) aListenerObject;
			aListener.feedComponentChanged(this);
		}
	}
}
