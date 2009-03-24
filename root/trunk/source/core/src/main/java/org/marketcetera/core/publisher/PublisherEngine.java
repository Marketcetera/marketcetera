package org.marketcetera.core.publisher;

import java.util.LinkedHashSet;
import java.util.concurrent.*;

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
     * A mirror of {@link #mSubscribers} kept for fast traversal when
     * publishing events without needing locks . The contents of this array are
     * never modified. To make any changes, a new copy of this array is created
     * and the reference is updated to point to the new array.
     */
    private volatile ISubscriber[] mSubscriberArray;
    /**
     * the pool of notifiers common to all <code>PublisherEngine</code> objects
     */
    private static final ExecutorService sNotifierPool = Executors.newCachedThreadPool();
    /**
     * indicates whether this publisher should do all publications synchronously or not
     */
    private final boolean mSynchronousNotification;
    /**
     * Create a new <code>PublisherEngine</code> object.
     * <p>
     * The engine can do publications synchronously or asynchronously.
     * <p>
     * When doing publications synchronously, all the subscribers are
     * notified in the same thread as the one invoking
     * {@link #publish(Object)}. The <code>publish()</code> method does not
     * return until all the subscribers have been notified.
     * <p>
     * When doing publications asynchronously, the subscribers are notified
     * in a separate thread from the one invoking {@link #publish(Object)}.
     * In this configuration <code>publish()</code> may return before or
     * after the subscribers have been notified. Moreover, the subscribers
     * may receive events in a different order from which they have been
     * published as the events are received by subscribers in different
     * threads.
     *
     * @param inSynchronousNotification if the publisher engine should
     * publish events synchronously or not.
     */
    public PublisherEngine(boolean inSynchronousNotification)
    {
        mSynchronousNotification = inSynchronousNotification;
    }    
    /**
     * Create a new <code>PublisherEngine</code> object.
     * <p>
     * Invoking this constructor is the same as invoking
     * <code>new PublisherEngine(false)</code>.
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
        return mSynchronousNotification;
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
     * <p>
     * This method will block until all publications are complete.
     * <p>
     * This method is no different from {@link #publish(Object)} if
     * {@link #isSynchronousNotification()} is true.
     *
     * @param inData an <code>Object</code> value
     * @throws InterruptedException if the thread is interrupted while waiting
     *   for notifications to complete
     * @throws ExecutionException if the subscriber threw an unexpected error. 
     */
    public void publishAndWait(Object inData) 
        throws InterruptedException, ExecutionException
    {
        Future<?> future = doPublish(inData);
        if (future != null) {
            future.get();
        }
    }      
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#subscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
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
            synchronizeSubscriberArray();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#unsubscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void unsubscribe(ISubscriber inSubscriber)
    {
        if(inSubscriber == null) {
            return;
        }
        synchronized(mSubscribers) {
            // don't have to worry if the subscriber is not present,
            //  Set takes care of that for us
            mSubscribers.remove(inSubscriber);
            synchronizeSubscriberArray();
        }
    }

    /**
     * Synchronizes the subscriber array with the list of subscribers.
     * <p>
     * This method should be invoked whenever subscribers list is modified.
     * The lock on subscribers list should be acquired before this method
     * is invoked. 
     */
    private void synchronizeSubscriberArray()  {
        mSubscriberArray = mSubscribers.toArray(
                new ISubscriber[mSubscribers.size()]);
    }
    /**
     * Perform the actual publication to subscribers.
     *
     * @param inData an <code>Object</code> value.
     *
     * @return a non-null future value if {@link #isSynchronousNotification()}
     * is true, a null value otherwise. 
     */
    private Future<?> doPublish(final Object inData)
    {
        final ISubscriber[] subscribers = mSubscriberArray;
        
        SLF4JLoggerProxy.debug(this,
                "Publishing {} to {} subscriber(s)", //$NON-NLS-1$
                inData, (subscribers == null ? 0 : subscribers.length));

        if(subscribers == null) {
            return null;
        }
        if (isSynchronousNotification()) {
            publishToSubscribers(subscribers, inData);
            return null;
        } else {
            // hand the notification chore to a thread from the thread pool
            return sNotifierPool.submit(new Runnable() {
                public void run() {
                    publishToSubscribers(subscribers, inData);
                }
            });
        }
    }

    /**
     * Publishes the supplied data object to the specified list of subscribers.
     *
     * @param inSubscribers The list of subscribers that need to be notified.
     * @param inData the data to publish to the subscribers.
     */
    private static void publishToSubscribers(ISubscriber[] inSubscribers,
                                             Object inData)
    {
        for (ISubscriber subscriber: inSubscribers) {
            try {
                if (subscriber.isInteresting(inData)) {
                    subscriber.publishTo(inData);
                }
            } catch (Throwable t) {
                SLF4JLoggerProxy.debug(PublisherEngine.class, t,
                        "Subscriber {} threw an exception during publication, skipping", //$NON-NLS-1$
                        subscriber);
            }
        }
    }
}
