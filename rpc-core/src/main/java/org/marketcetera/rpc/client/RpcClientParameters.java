package org.marketcetera.rpc.client;

import org.marketcetera.core.ClientParameters;

/* $License$ */

/**
 * Provides parameters necessary to connect to an RPC server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RpcClientParameters
        extends ClientParameters
{
    /**
     * Get the heartbeat interval value in millis.
     *
     * @return a <code>long</code> value
     */
    long getHeartbeatInterval();
    /**
     * Get the interval in millis to wait for an orderly shutdown.
     *
     * @return a <code>long</code> value
     */
    long getShutdownWait();
}
