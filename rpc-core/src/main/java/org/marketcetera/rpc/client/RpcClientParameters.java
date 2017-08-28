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
     * 
     *
     *
     * @return
     */
    long getHeartbeatInterval();
    /**
     * 
     *
     *
     * @return
     */
    long getShutdownWait();
}
