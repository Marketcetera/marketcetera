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
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#useSsl()
     */
    @Override
    public boolean useSsl()
    {
        return useSsl;
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
     * Sets the useSsl value.
     *
     * @param inUseSsl a <code>boolean</code> value
     */
    public void setUseSsl(boolean inUseSsl)
    {
        useSsl = inUseSsl;
    }
    /**
     * indicates if the client connection should use SSL
     */
    private boolean useSsl = false;
    /**
     * heartbeat interval value
     */
    private long heartbeatInterval = 5000;
    /**
     * shutdown wait value
     */
    private long shutdownWait = 5000;
}
