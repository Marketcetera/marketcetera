package org.marketcetera.core.publisher;

/**
 * Test implementation of {@link Publisher}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class TestPublisher
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
}
