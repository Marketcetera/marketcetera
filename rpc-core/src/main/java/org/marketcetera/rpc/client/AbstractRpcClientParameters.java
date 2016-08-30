package org.marketcetera.rpc.client;

import java.util.Locale;

/* $License$ */

/**
 * Provides common RPC client parameter behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractRpcClientParameters
        implements RpcClientParameters
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#setHostname()
     */
    @Override
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getHostname()
     */
    @Override
    public String getHostname()
    {
        return hostname;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#setPort(int)
     */
    @Override
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#setPassword(java.lang.String)
     */
    @Override
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale inLocale)
    {
        locale = inLocale;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        return locale;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#setHeartbeatInterval(long)
     */
    @Override
    public void setHeartbeatInterval(long inHeartbeatInterval)
    {
        heartbeatInterval = inHeartbeatInterval;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#getHeartbeatInterval()
     */
    @Override
    public long getHeartbeatInterval()
    {
        return heartbeatInterval;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientParameters#setShutdownWait(long)
     */
    @Override
    public void setShutdownWait(long inShutdownWait)
    {
        shutdownWait = inShutdownWait;
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
     * 
     */
    private String hostname;
    /**
     * 
     */
    private int port;
    /**
     * 
     */
    private String username;
    /**
     * 
     */
    private String password;
    /**
     * 
     */
    private Locale locale = Locale.getDefault();
    /**
     * 
     */
    private long heartbeatInterval = 5000;
    /**
     * 
     */
    private long shutdownWait = 5000;
}
