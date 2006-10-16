package org.marketcetera.orderloader;

import org.marketcetera.core.FeedComponent;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.jms.JMSAdapter;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Session;
import java.util.List;
import java.util.LinkedList;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class JMSConnector implements FeedComponent {

    public static final String JMS_CONNECTOR_ID = "Message Queue";

    private JMSAdapter adapter;

    private String outgoingQueueName;

    private FeedStatus feedStatus = FeedStatus.UNKNOWN;

    private List<IFeedComponentListener> listeners = new LinkedList<IFeedComponentListener>();

    JMSConnector() {
    }

    public void init(String outgoingQueueName,
                     String contextFactoryName, String providerUrl,
                     String connectionFactoryName) throws JMSException {
        try {
            adapter = new JMSAdapter(contextFactoryName, providerUrl,
                    connectionFactoryName);
            adapter.start();
            adapter.connectOutgoingQueue(outgoingQueueName, outgoingQueueName,
                    Session.AUTO_ACKNOWLEDGE);
            this.outgoingQueueName = outgoingQueueName;
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

    private void setStatus(FeedComponent.FeedStatus theStatus){
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
        for (IFeedComponentListener aListener : listeners) {
            aListener.feedComponentChanged(this);
        }
    }
}
