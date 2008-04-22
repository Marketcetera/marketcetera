package org.marketcetera.core.publisher;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.marketcetera.core.LoggerAdapter;

/**
 * Publication engine which supplies the Publish side of the Publish/Subscribe contract.
 * 
 * <p>This object is meant to be used by means of a "has-a" relationship.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public final class PublisherEngine
    implements IPublisher
{
    /**
     * the queue of subscribers - should be maintained in FIFO order
     */
    private final LinkedHashSet<ISubscriber> mSubscribers;    
    /**
     * the pool of notifiers common to all <code>PublisherEngine</code> objects
     */
    private static ExecutorService sNotifierPool;
    /**
     * the max pool size for notifier threads - increase this for greater performance, decrease for smaller footprint
     */
    private final int mMaxPoolSize;
    /**
     * default max pool size for notifier threads
     */
    private static final int DEFAULT_MAX_POOL_SIZE = 20;    
    /**
     * Initializes the publisher threadpool if necessary.
     */
    private static synchronized void initializeThreadPool(int inMaxPoolSize)
    {
        if(sNotifierPool != null) {
            return;
        }
        sNotifierPool = Executors.newFixedThreadPool(inMaxPoolSize);        
    }  
    /**
     * Create a new <code>PublisherEngine</code> object.
     */
    public PublisherEngine()
    {
        this(DEFAULT_MAX_POOL_SIZE);
    }    
    /**
     * Create a new <code>PublisherEngine</code> object.
     * 
     * @param inMaxPoolSize an <code>int</code> value
     */
    public PublisherEngine(int inMaxPoolSize)
    {
        mSubscribers = new LinkedHashSet<ISubscriber>();
        mMaxPoolSize = inMaxPoolSize;
    }    
    /**
     * Advertise for publication the given object to all subscribers.
     * 
     * @param inData an <code>Object</code> value
     */
    public void publish(Object inData)
    {
        doPublish(inData);
    }
    /**
     * Advertise for publication the given object to all subscribers and wait
     * until all publications are done.
     *
     * <P>This method will block until all publications are complete.
     *
     * @param inData an <code>Object</code> value
     * @throws InterruptedException if the thread is interrupted while waiting
     *   for notifications to complete
     * @throws ExecutionException 
     */
    public void publishAndWait(Object inData) 
        throws InterruptedException, ExecutionException
    {
        doPublish(inData).get();
    }      
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#subscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    public void subscribe(ISubscriber inSubscriber)
    {
        if(inSubscriber == null) {
            return;
        }
        synchronized(mSubscribers) {
            // don't have to worry if the subscriber is already present,
            //  according to the LinkedHashSet contract, reinsertion does
            //  not affect order
            mSubscribers.add(inSubscriber);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#unsubscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    public void unsubscribe(ISubscriber inSubscriber)
    {
        if(inSubscriber == null) {
            return;
        }
        synchronized(mSubscribers) {
            // don't have to worry if the subscriber is not present,
            //  Set takes care of that for us
            mSubscribers.remove(inSubscriber);
        }
    }
    /**
     * Gets the max pool size setting.
     * 
     * @return an <code>int</code> value indicating the maximum number of notifier threads the publisher will use
     */
    private int getMaxPoolSize()
    {
        return mMaxPoolSize;
    }
    /**
     * Perform the actual publication to subscribers.
     *
     * @param inData an <code>Object</code> value
     * @return a <code>Future&lt;Object&gt;</code> value
     */
    private Future<Object> doPublish(Object inData)
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Publishing " + inData + " to subscribers", 
                            this);
        }
        initializeThreadPool(getMaxPoolSize());
        // hand the notification chore to a thread from the thread pool
        List<ISubscriber> subscribers;
        synchronized(mSubscribers) {
            subscribers = new ArrayList<ISubscriber>(mSubscribers);
        }
        return sNotifierPool.submit(new PublisherEngineNotifier(subscribers,
                                                                inData));
    }
}
