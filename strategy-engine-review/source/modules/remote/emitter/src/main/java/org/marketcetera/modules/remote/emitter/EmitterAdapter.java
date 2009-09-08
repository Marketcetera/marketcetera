package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * An adapter that is provided to the RemoteDataEmitter to receive
 * data and connection status notifications from it. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface EmitterAdapter {
    /**
     * Provides the data received from the remote source.
     *
     * @param inObject the data received from the remote source.
     */
    public void receiveData(Object inObject);

    /**
     * Provides a notification on the change of connection status.
     *
     * @param inOldStatus the old connection status.
     * @param inNewStatus the new connection status.
     */
    public void connectionStatusChanged(boolean inOldStatus, boolean inNewStatus);
}
