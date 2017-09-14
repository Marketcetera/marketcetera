package org.marketcetera.dataflow.client;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * This interface enables the clients to receive all the data emitted
 * by the strategies running on the strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id: DataReceiver.java 17223 2016-08-31 01:03:01Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DataReceiver.java 17223 2016-08-31 01:03:01Z colin $")
public interface DataReceiver {
    /**
     * Provides the data received from the remote source.
     *
     * @param inObject the data received from the remote source.
     */
    public void receiveData(Object inObject);
}
