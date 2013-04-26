package org.marketcetera.api.systemmodel;

/**
 * Capable of responding to publish requests and receiving publications.
 *
 * <p>After subscribing to a {@link Publisher}, when the {@link Publisher} has
 * an update to offer, the {@link Publisher} will ask if the update is interesting,
 * updating the <code>Subscriber</code> if appropriate.
 * 
 * @version $Id$
 * @see Publisher
 */
public interface Subscriber
{
    /**
     * Receives an update from the <code>Publisher</code>.
     * 
     * @param inData an <code>Object</code> value containing the implementation-dependent update
     */
    public void publishTo(Object inData);
}
