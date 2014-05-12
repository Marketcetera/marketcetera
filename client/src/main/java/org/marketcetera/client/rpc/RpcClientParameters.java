package org.marketcetera.client.rpc;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.client.ClientParameters;
import org.marketcetera.trade.TradeContextClassProvider;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * The set of parameters needed to initialize the RPC client.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public class RpcClientParameters
        extends ClientParameters
{
    /**
     * Create a new RpcClientParameters instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>char[]</code> value
     * @param inURL a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inIDPrefix a <code>String</code> value
     * @param inHeartbeatInterval an <code>int</code> value
     */
    public RpcClientParameters(String inUsername,
                               char[] inPassword,
                               String inURL,
                               String inHostname,
                               int inPort,
                               String inIDPrefix,
                               int inHeartbeatInterval)
    {
        super(inUsername,
              inPassword,
              inURL,
              inHostname,
              inPort,
              inIDPrefix,
              inHeartbeatInterval);
    }
    /**
     * Create a new RpcClientParameters instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>char[]</code> value
     * @param inURL a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inIDPrefix a <code>String</code> value
     */
    public RpcClientParameters(String inUsername,
                               char[] inPassword,
                               String inURL,
                               String inHostname,
                               int inPort,
                               String inIDPrefix)
    {
        super(inUsername,
              inPassword,
              inURL,
              inHostname,
              inPort,
              inIDPrefix);
    }
    /**
     * Create a new RpcClientParameters instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>char[]</code> value
     * @param inURL a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     */
    public RpcClientParameters(String inUsername,
                               char[] inPassword,
                               String inURL,
                               String inHostname,
                               int inPort)
    {
        super(inUsername,
              inPassword,
              inURL,
              inHostname,
              inPort);
    }
    /**
     * Get the useJms value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getUseJms()
    {
        return useJms;
    }
    /**
     * Sets the useJms value.
     *
     * @param inUseJms a <code>boolean</code> value
     */
    public void setUseJms(boolean inUseJms)
    {
        useJms = inUseJms;
    }
    /**
     * Get the threadPoolCore value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadPoolCore()
    {
        return threadPoolCore;
    }
    /**
     * Sets the threadPoolCore value.
     *
     * @param inThreadPoolCore an <code>int</code> value
     */
    public void setThreadPoolCore(int inThreadPoolCore)
    {
        threadPoolCore = inThreadPoolCore;
    }
    /**
     * Get the threadPoolMax value.
     *
     * @return an <code>int</code> value
     */
    public int getThreadPoolMax()
    {
        return threadPoolMax;
    }
    /**
     * Sets the threadPoolMax value.
     *
     * @param inThreadPoolMax an <code>int</code> value
     */
    public void setThreadPoolMax(int inThreadPoolMax)
    {
        threadPoolMax = inThreadPoolMax;
    }
    /**
     * Get the sendBufferSize value.
     *
     * @return an <code>int</code> value
     */
    public int getSendBufferSize()
    {
        return sendBufferSize;
    }
    /**
     * Sets the sendBufferSize value.
     *
     * @param inSendBufferSize an <code>int</code> value
     */
    public void setSendBufferSize(int inSendBufferSize)
    {
        sendBufferSize = inSendBufferSize;
    }
    /**
     * Get the receiveBufferSize value.
     *
     * @return an <code>int</code> value
     */
    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }
    /**
     * Sets the receiveBufferSize value.
     *
     * @param inReceiveBufferSize an <code>int</code> value
     */
    public void setReceiveBufferSize(int inReceiveBufferSize)
    {
        receiveBufferSize = inReceiveBufferSize;
    }
    /**
     * Get the noDelay value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getNoDelay()
    {
        return noDelay;
    }
    /**
     * Sets the noDelay value.
     *
     * @param inNoDelay a <code>boolean</code> value
     */
    public void setNoDelay(boolean inNoDelay)
    {
        noDelay = inNoDelay;
    }
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /**
     * providers context classes for serialization
     */
    private ContextClassProvider contextClassProvider = TradeContextClassProvider.INSTANCE;
    /**
     * activate or deactive JMS
     */
    private boolean useJms = true;
    /**
     * thread pool core size
     */
    private int threadPoolCore = 10;
    /**
     * max thread pool size
     */
    private int threadPoolMax = 200;
    /**
     * rpc send buffer size
     */
    private int sendBufferSize = 1048576;
    /**
     * rpc receive buffer size
     */
    private int receiveBufferSize = 1048576;
    /**
     * activates or deactivates Nagle's algorithm
     */
    private boolean noDelay = true;
}
