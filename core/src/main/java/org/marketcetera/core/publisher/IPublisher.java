package org.marketcetera.core.publisher;

import java.util.concurrent.ExecutionException;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Capable of supplying updates to a group of {@link Subscriber} objects.
 * 
 * <p>Implementers are guaranteed to be notified in subscription order. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
public interface IPublisher
{
    /**
     * Subscribe to updates from the <code>Publisher</code>.
     * 
     * <p>If the same <code>Subscriber</code> is passed more than once to this method
     * the result is the same as if the <code>Subscriber</code> were passed only
     * once: the <code>Subscriber</code> receives one update in the same order as
     * indicated by the <code>Subscriber</code> queue when the <code>Subscriber</code>
     * instance was first passed.
     * 
     * @param inSubscriber a <code>Subscriber</code> value
     */
    public void subscribe(Subscriber inSubscriber);
    
    /**
     * Unsubscribe to updates from the <code>Publisher</code>.
     * 
     * <p>If the <code>Subscriber</code> passed is not a current subscriber,
     * this method has no effect.
     * 
     * @param inSubscriber a <code>Subscriber</code> value
     */
    public void unsubscribe(Subscriber inSubscriber);
    /**
     * Publish the given data to subscribers.
     *
     * @param inData an <code>Object</code> value
     */
    public void publish(Object inData);
    /**
     * Publish the given data to subscribers and wait until all subscribers have been notified.
     *
     * @param inData an <code>Object</code> value
     * @throws InterruptedException if the thread is interrupted before notifications are complete
     * @throws ExecutionException if an error occurs during notification
     */
    public void publishAndWait(Object inData) 
            throws InterruptedException, ExecutionException;
    /**
     * Gets the number of subscribers.
     *
     * @return an <code>int</code> value
     */
    public int getSubscriptionCount();
}
