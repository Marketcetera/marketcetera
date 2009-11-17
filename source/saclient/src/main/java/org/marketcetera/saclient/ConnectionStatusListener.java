package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * This interface enables clients to be notified whenever the status
 * of client's connection to the strategy agent changes.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface ConnectionStatusListener {
    /**
     * Provides the current connection status.
     *
     * @param inStatus true if the client got connected, false if the client
     * got disconnected.
     */
    public void receiveConnectionStatus(boolean inStatus);
}
