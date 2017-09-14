package org.marketcetera.dataflow.client;

/* $License$ */

/**
 * Broadcasts data received from remote clients.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DataBroadcaster
{
    /**
     * Receive the given data and broadcast it.
     *
     * @param inData an <code>Object</code> value
     */
    void receiveData(Object inData);
}
