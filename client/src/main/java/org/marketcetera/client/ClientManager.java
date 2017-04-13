package org.marketcetera.client;

import java.util.Set;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Sets;


/* $License$ */
/**
 * Abstraction that manages the initialization of the Client and provides
 * an easy way to get to its singleton instance.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class ClientManager
{
    /**
     * Initializes the connection to the server. The handle to communicate
     * with the server can be obtained via {@link #getInstance()}.
     *
     * @param inParameter The parameters to connect the client. Cannot be null.
     *
     * @throws ConnectionException if there were errors connecting
     * to the server.
     * @throws ClientInitException if the client is already initialized.
     */
    public static synchronized void init(ClientParameters inParameter)
            throws ConnectionException, ClientInitException
    {
        if(!isInitialized()) {
            mClient = mClientFactory.getClient(inParameter);
            notifyClientInitListeners();
        } else {
            throw new ClientInitException(Messages.CLIENT_ALREADY_INITIALIZED);
        }
    }
    /**
     * Sets the <code>ClientFactory</code> to use to create the <code>Client</code>.
     *
     * @param inFactory a <code>ClientFactory</code> value
     * @throws ClientInitException if the client is already initialized.
     */
    public static synchronized void setClientFactory(ClientFactory inFactory)
            throws ClientInitException
    {
        if(isInitialized()) {
            throw new ClientInitException(Messages.CLIENT_ALREADY_INITIALIZED);
        }
        mClientFactory = inFactory;
    }
    /**
     * Returns the Client instance after it has been initialized via
     * {@link #init(ClientParameters)}
     *
     * @return the client instance to communicate with the server.
     *
     * @throws ClientInitException if the client is not initialized.
     */
    public static Client getInstance() throws ClientInitException {
        if (isInitialized()) {
            return mClient;
        } else {
            throw new ClientInitException(Messages.CLIENT_NOT_INITIALIZED);
        }
    }

    /**
     * Returns true if the client is initialized, false if it's not.
     *
     * @return if the client is initialized.
     */
    public static boolean isInitialized() {
        return mClient != null;
    }
    /**
     * Add the given client listener.
     *
     * @param inClientInitListener a <code>ClientInitListener</code> value
     */
    public static void addClientInitListener(ClientInitListener inClientInitListener)
    {
        if(isInitialized()) {
            notifyClientInitListener(inClientInitListener);
        } else {
            synchronized(clientInitListeners) {
                clientInitListeners.add(inClientInitListener);
            }
        }
    }
    /**
     * Remove the given client listener.
     *
     * @param inClientInitListener a <code>ClientInitListener</code> value
     */
    public static void removeClientInitListener(ClientInitListener inClientInitListener)
    {
        synchronized(clientInitListeners) {
            clientInitListeners.remove(inClientInitListener);
        }
    }
    /**
     * Resets the client to the uninitialized state. This method is invoked
     * by the client implementation when it's {@link Client#close() closed}.
     * This method is not meant to be used by clients. 
     */
    synchronized static void reset() {
        mClient = null;
    }
    /**
     * Notify all client listeners that the client has been initialized.
     */
    private static void notifyClientInitListeners()
    {
        synchronized(clientInitListeners) {
            for(ClientInitListener clientInitListener : clientInitListeners) {
                notifyClientInitListener(clientInitListener);
            }
        }
    }
    /**
     * Notify the given listener that the client has been initialized.
     *
     * @param inClientInitListener a <code>ClientInitListener</code> value
     */
    private static void notifyClientInitListener(ClientInitListener inClientInitListener)
    {
        try {
            inClientInitListener.receiveClient(mClient);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(ClientManager.class,
                                  e);
        }
    }
    /**
     * Do not allow any instances to be created.
     */
    private ClientManager() {
    }
    /**
     * the <code>ClientFactory</code> to use to create the <code>Client</code> object 
     */
    private volatile static ClientFactory mClientFactory = new ClientFactory()
    {
        @Override
        public Client getClient(ClientParameters inParameters)
                throws ClientInitException,ConnectionException
        {
            return new ClientImpl(inParameters);
        }
    };
    /**
     * the <code>Client</code> object
     */
    private volatile static Client mClient;
    /**
     * holds subscribed client init listeners
     */
    private final static Set<ClientInitListener> clientInitListeners = Sets.newLinkedHashSet();
}
