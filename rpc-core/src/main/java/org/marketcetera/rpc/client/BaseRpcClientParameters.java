package org.marketcetera.rpc.client;

import org.marketcetera.core.BaseClientParameters;

/* $License$ */

/**
 * Provides common RPC client parameter behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BaseRpcClientParameters
        extends BaseClientParameters
        implements RpcClientParameters
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getHeartbeatInterval()
     */
    @Override
    public long getHeartbeatInterval()
    {
        return heartbeatInterval;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getShutdownWait()
     */
    @Override
    public long getShutdownWait()
    {
        return shutdownWait;
    }
    /**
     * Sets the heartbeatInterval value.
     *
     * @param inHeartbeatInterval a <code>long</code> value
     */
    public void setHeartbeatInterval(long inHeartbeatInterval)
    {
        heartbeatInterval = inHeartbeatInterval;
    }
    /**
     * Sets the shutdownWait value.
     *
     * @param inShutdownWait a <code>long</code> value
     */
    public void setShutdownWait(long inShutdownWait)
    {
        shutdownWait = inShutdownWait;
    }
    /**
     * heartbeat interval value
     */
    private long heartbeatInterval = 5000;
    /**
     * shutdown wait value
     */
    private long shutdownWait = 5000;
}
