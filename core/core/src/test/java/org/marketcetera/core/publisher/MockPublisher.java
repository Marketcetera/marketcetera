package org.marketcetera.core.publisher;

import java.util.concurrent.ExecutionException;

/**
 * Test implementation of {@link Publisher}.
 *
 * @version $Id: MockPublisher.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.43-SNAPSHOT
 */
public class MockPublisher
        implements Publisher
{
    private PublisherEngine mEngine = new PublisherEngine();
    
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.Publisher#subscribe(org.marketcetera.core.publisher.Subscriber)
     */
    public void subscribe(Subscriber inSubscriber)
    {
        mEngine.subscribe(inSubscriber);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.Publisher#unsubscribe(org.marketcetera.core.publisher.Subscriber)
     */
    public void unsubscribe(Subscriber inSubscriber)
    {
        mEngine.unsubscribe(inSubscriber);
    }
    
    public PublisherEngine getEngine()
    {
        return mEngine;
    }
    
    public void publish(Object inData)
    {
        mEngine.publish(inData);
    }
    
    public void publishAndWait(Object inData) 
        throws InterruptedException, ExecutionException
    {
        mEngine.publishAndWait(inData);
    }
}
