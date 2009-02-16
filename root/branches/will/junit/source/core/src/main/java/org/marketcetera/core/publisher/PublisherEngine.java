package org.marketcetera.core.publisher;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * Publication engine which supplies the Publish side of the Publish/Subscribe contract.
 * 
 * <p>This object is meant to be used by means of a "has-a" relationship.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public final class PublisherEngine
    implements IPublisher
{
    /**
     * the queue of subscribers - should be maintained in FIFO order
     */
    private final LinkedHashSet<ISubscriber> mSubscribers = new LinkedHashSet<ISubscriber>();    
    /**
     * the pool of notifiers common to all <code>PublisherEngine</code> objects
     */
    private static final ExecutorService sNotifierPool = Executors.newCachedThreadPool();
    /**
     * indicates whether this publisher should do all publications synchronously or not
     */
    private final boolean doSynchronousNotification;
    /**
     * Create a new <code>PublisherEngine</code> object.
     */
    public PublisherEngine(boolean inDoSynchronousNotification)
    {
        doSynchronousNotification = inDoSynchronousNotification;
    }    
    /**
     * Create a new <code>PublisherEngine</code> object.
     */
    public PublisherEngine()
    {
        this(false);
    }    
    /**
     * Indicates whether the publisher is notifying synchronously.
     *
     * @return a <code>PublisherEngine</code> value
     */
    public boolean isSynchronousNotification()
    {
        return doSynchronousNotification;
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
     * Perform the actual publication to subscribers.
     *
     * @param inData an <code>Object</code> value
     * @return a <code>Future&lt;Object&gt;</code> value
     */
    private Future<Object> doPublish(Object inData)
    {
        SLF4JLoggerProxy.debug(this, "Publishing {} to subscribers", inData); //$NON-NLS-1$
        // hand the notification chore to a thread from the thread pool
        List<ISubscriber> subscribers;
        synchronized(mSubscribers) {
            subscribers = new ArrayList<ISubscriber>(mSubscribers);
        }
        Future<Object> token = sNotifierPool.submit(new PublisherEngineNotifier(subscribers,
                                                                                inData));
        if(isSynchronousNotification()) {
            try {
                token.get();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(PublisherEngine.class,
                                      e);
            }
        }
        return token;
    }
}
