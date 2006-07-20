package org.marketcetera.quotefeed;

import org.marketcetera.core.FeedComponent;

/**
 * 
 */

public interface MessageSubscriber extends FeedComponent {

    public MessageListener getMessageListener();

    public void setMessageListener(MessageListener messageListener);

    public MessageSelector getMessageSelector();

    public void setMessageSelector(MessageSelector selector);

    public FeedMessage receive() throws QuoteFeedException;

    public FeedMessage receive(long timeout) throws QuoteFeedException;

    public void receiveNoWait(FeedMessage [] into) throws QuoteFeedException;

    public void start() throws QuoteFeedException;

    public void stop() throws QuoteFeedException;

    public void close() throws QuoteFeedException;

}