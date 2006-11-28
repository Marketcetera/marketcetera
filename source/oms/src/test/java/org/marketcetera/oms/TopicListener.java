package org.marketcetera.oms;

import quickfix.Message;
import quickfix.field.Side;

import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ArrayBlockingQueue;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.spring.JMSFIXMessageConverter;
import junit.framework.Assert;

/**
 * Dummy listener on a topic and a blocking queue that keeps track of all the received messages
 * 
 * @author toli
* @version $Id$
*/
public class TopicListener {
    private ArrayBlockingQueue<Message> topicMsgs;
    
    public TopicListener(){}

    public TopicListener(ArrayBlockingQueue<Message> inTopicMsgs)
    {
        topicMsgs = inTopicMsgs;
    }

    public void onMessage(Message message) {
        topicMsgs.add(message);
    }
}
