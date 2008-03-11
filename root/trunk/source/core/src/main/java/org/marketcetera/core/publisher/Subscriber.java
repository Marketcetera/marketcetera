package org.marketcetera.core.publisher;

/**
 * Capable of responding to publish requests and receiving publications.
 *
 * <p>After subscribing to a {@link Publisher}, when the {@link Publisher} has
 * an update to offer, the {@link Publisher} will ask if the update is interesting,
 * updating the <code>Subscriber</code> if appropriate.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @see Publisher
 */
public interface Subscriber
{
    /**
     * Determines if the <code>Subscriber</code> would be interested in receiving the given object as an update.
     * 
     * <p>If this method throws an exception, the <code>Publisher</code> will not publish this update 
     * to this <code>Subscriber</code>.
     * 
     * @param inData an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isInteresting(Object inData);
    
    /**
     * Receives an update from the <code>Publisher</code>.
     * 
     * <p>This method will be called only if the <code>Subscriber</code> returns true from {@link Subscriber#isInteresting(Object)}.
     * 
     * @param inData an <code>Object</code> value containing the implementation-dependent update
     */
    public void publishTo(Object inData);
}
