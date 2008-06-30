package org.marketcetera.core.publisher;

/**
 * Capable of supplying updates to a group of {@link ISubscriber} objects.
 * 
 * <p>Implementers are guaranteed to be notified in subscription order. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
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
    public void subscribe(ISubscriber inSubscriber);
    
    /**
     * Unsubscribe to updates from the <code>Publisher</code>.
     * 
     * <p>If the <code>Subscriber</code> passed is not a current subscriber,
     * this method has no effect.
     * 
     * @param inSubscriber a <code>Subscriber</code> value
     */
    public void unsubscribe(ISubscriber inSubscriber);
}
