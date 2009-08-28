package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * This interface enables the clients to receive all the data emitted
 * by the strategies running on the strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface DataReceiver {
    /**
     * Provides the data received from the remote source.
     *
     * @param inObject the data received from the remote source.
     */
    public void receiveData(Object inObject);
}
