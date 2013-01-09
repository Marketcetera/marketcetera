package org.marketcetera.core.publisher;

import java.util.concurrent.ExecutionException;

/**
 * Test implementation of {@link IPublisher}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class MockPublisher
        implements IPublisher
{
    private PublisherEngine mEngine = new PublisherEngine();
    
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.Publisher#subscribe(org.marketcetera.core.publisher.Subscriber)
     */
    public void subscribe(ISubscriber inSubscriber)
    {
        mEngine.subscribe(inSubscriber);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.Publisher#unsubscribe(org.marketcetera.core.publisher.Subscriber)
     */
    public void unsubscribe(ISubscriber inSubscriber)
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
