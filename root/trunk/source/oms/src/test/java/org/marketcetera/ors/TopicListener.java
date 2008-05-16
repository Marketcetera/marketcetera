package org.marketcetera.ors;

import quickfix.Message;

import java.util.concurrent.ArrayBlockingQueue;

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
