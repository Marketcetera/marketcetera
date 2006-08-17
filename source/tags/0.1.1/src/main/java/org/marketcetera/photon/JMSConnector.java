package org.marketcetera.photon;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.eclipse.core.runtime.ListenerList;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.jms.JMSAdapter;
import org.marketcetera.quickfix.QuickFIXDescriptor;

/**
 * The JMSConnector encapsulates the connection to a JMS message server.
 * It has one incoming topic, and one outgoing queue, to communicate
 * in both directions with the OMS.  It provides two methods for sending
 * messages to the queue one for QuickFIX messages and one for already-formatted
 * JMS messages.  It also provides a method for registering as a listener
 * for messages on the incoming Topic.
 * 
 * JMS connector also implements the FeedComponent interface, allowing
 * the application to display its status to the end user.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class JMSConnector implements FeedComponent {

	public static final String JMS_CONNECTOR_ID = "Message Queue";

	private JMSAdapter adapter;

	private String outgoingQueueName;

	private String incomingTopicName;

	private FeedStatus feedStatus = FeedStatus.UNKNOWN;

	private ListenerList listeners = new ListenerList();

	/**
	 * Create a new JMSConnector.  Callers must call {@link #init(String, String, String, String, String)}
	 * before using the connector.
	 */
	JMSConnector() {
	}

	/**
	 * Initializes this JMSConnector to communicate with the OMS via JMS connection.
	 * 
	 * @param incomingTopicName the name of the topic on which to listen for incoming messages
	 * @param outgoingQueueName the name of the queue to which to send outgoing messages
	 * @param contextFactoryName the name of the JMS context factory (see documentation for your JMS server)
	 * @param providerUrl the URL to which to connect for the JMS server
	 * @param connectionFactoryName the name of the JMS connection factory class (see documentation for your JMS server)
	 * @throws JMSException if there is an error connecting to the JMS server, or establishing connections to the queue or topic
	 */
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
	
	/**
	 * Shuts down the connection to the JMS server.  After this method is called,
	 * you must again call {@link #init(String, String, String, String, String)} before
	 * using the JMSConnector again.
	 */
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

	/**
	 * Send a fully formed JMS message to the outgoing queue.  This method will 
	 * send the given message unmodified to the outgoing queue specified in the 
	 * {@link #init(String, String, String, String, String)} method.
	 * @param aMessage the message to send to the queue
	 * @throws JMSException if there was an error sending the message
	 */
	public void sendToQueue(javax.jms.Message aMessage) throws JMSException {
		// TODO: check feed status before sending?
		try {
			adapter.getOutgoingQueueSender(outgoingQueueName).send(aMessage);
		} catch (JMSException e) {
			setStatus(FeedStatus.ERROR);
			throw e;
		}
	}

	
	/**
	 * Send a QuickFIX message to the outgoing queue. This method will
	 * format the QuickFIX message into a string by calling {@link quickfix.Message#toString()}
	 * on the message parameter, and placing the result into a JMS {@link TextMessage}
	 * @param aMessage the QuickFIX message to send to the output queue
	 * @throws JMSException if there was an error sending the message to the output queue
	 */
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

	/**
	 * Register the given {@link javax.jms.MessageListener} as a listener for messages that
	 * come in on the JMS topic.  All messages received on the topic specified in
	 * {@link #init(String, String, String, String, String)} will be passed on to the 
	 * specified listener.  Subsequent calls to this method will replace the original
	 * listener with the newly specified listener.
	 * @param listener the listener for messages on the incoming topic
	 * @throws JMSException if there was an error setting the topic listener
	 */
	public void setTopicListener(MessageListener listener) throws JMSException {
		try {
			adapter.getIncomingTopicSubscriber(incomingTopicName)
					.setMessageListener(listener);
		} catch (JMSException e) {
			setStatus(FeedStatus.ERROR);
			throw e;
		}
	}
	
	/**
	 * Internal helper method for setting the status of this FeedComponent.
	 * Takes care of notifying the listeners of the new FeedStatus.
	 * @param theStatus the new status for the FeedComponent
	 */
	private void setStatus(FeedStatus theStatus){
		feedStatus = theStatus;
		fireFeedComponentChanged();
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.core.FeedComponent#getFeedType()
	 */
	public FeedType getFeedType() {
		return FeedType.SIMULATED;
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.core.FeedComponent#getFeedStatus()
	 */
	public FeedStatus getFeedStatus() {
		return feedStatus;
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.core.FeedComponent#getID()
	 */
	public String getID() {
		// TODO Auto-generated method stub
		return JMS_CONNECTOR_ID;
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.core.FeedComponent#addFeedComponentListener(org.marketcetera.core.IFeedComponentListener)
	 */
	public void addFeedComponentListener(IFeedComponentListener arg0) {
		listeners.add(arg0);
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.core.FeedComponent#removeFeedComponentListener(org.marketcetera.core.IFeedComponentListener)
	 */
	public void removeFeedComponentListener(IFeedComponentListener arg0) {
		listeners.remove(arg0);
	}
	
	/**
	 * Helper method to notify IFeedComponentListeners of changes in the
	 * state of this feed component.
	 */
	private void fireFeedComponentChanged()
	{
		for (Object aListenerObject : listeners.getListeners()) {
			IFeedComponentListener aListener = (IFeedComponentListener) aListenerObject;
			aListener.feedComponentChanged(this);
		}
	}
}
