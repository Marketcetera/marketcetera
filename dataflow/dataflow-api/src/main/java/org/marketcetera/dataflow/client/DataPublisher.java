package org.marketcetera.dataflow.client;

/* $License$ */

/**
 * Manages data listeners.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DataPublisher
{
    /**
     * Adds a data receiver so that it can receive all the data received
     * from the remote source that this client is connected to.
     * <p>
     * If the same receiver is added more than once, it will receive
     * data as many times as it's been added.
     * <p>
     * The receivers are notified in the reverse order of their addition.
     *
     * @param inReceiver the receiver to add. Cannot be null.
     */
    void addDataReceiver(DataReceiver inReceiver);
    /**
     * Removes a data receiver that was previously added so that it no longer
     * receives data from the remote source.
     * <p>
     * If the receiver was added more than once, only its most
     * recently added occurrence will be removed.
     *
     * @param inReceiver the receiver to remove. Cannot be null.
     */
    void removeDataReceiver(DataReceiver inReceiver);
}
