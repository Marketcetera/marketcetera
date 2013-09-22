package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Abstraction that manages the initialization of the Client and provides
 * an easy way to get to its singleton instance.
 *
 * @author anshul@marketcetera.com
 * @author <a href="colin@marketcetera.com">Colin DuPlantis</a>
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
            ClientManager instance = getManagerInstance();
            instance.mClient = instance.mClientFactory.getClient(inParameter);
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
        ClientManager instance = getManagerInstance();
        instance.mClientFactory = inFactory;
    }
    /**
     * Returns the Client instance after it has been initialized via
     * {@link #init(ClientParameters)}
     *
     * @return the client instance to communicate with the server.
     *
     * @throws ClientInitException if the client is not initialized.
     */
    public static synchronized Client getInstance()
            throws ClientInitException
    {
        if(isInitialized()) {
            ClientManager instance = getManagerInstance();
            return instance.mClient;
        } else {
            throw new ClientInitException(Messages.CLIENT_NOT_INITIALIZED);
        }
    }
    /**
     * Returns true if the client is initialized, false if it's not.
     *
     * @return if the client is initialized.
     */
    public static synchronized boolean isInitialized()
    {
        ClientManager instance = getManagerInstance();
        return instance.mClient != null;
    }
    /**
     * Create a new ClientManager instance.
     * 
     * @throws IllegalStateException if a <code>ClientManager</code> instance has already been created 
     */
    public ClientManager()
    {
        synchronized(ClientManager.class) {
            if(instance != null) {
                throw new IllegalStateException(Messages.CLIENT_ALREADY_INITIALIZED.getText());
            }
            instance = this;
        }
    }
    /**
     * Resets the client to the uninitialized state. This method is invoked
     * by the client implementation when it's {@link Client#close() closed}.
     * This method is not meant to be used by clients. 
     */
    synchronized static void reset()
    {
        ClientManager instance = getManagerInstance();
        instance.mClient = null;
    }
    /**
     * Gets the <code>ClientManager</code> instance
     *
     * @return a <code>ClientManager</code> value
     */
    public static synchronized ClientManager getManagerInstance()
    {
        if(instance == null) {
            instance = new ClientManager();
        }
        return instance;
    }
    /**
     * the <code>ClientFactory</code> to use to create the <code>Client</code> object 
     */
    private volatile ClientFactory mClientFactory = new ClientFactory() {
        @Override
        public Client getClient(ClientParameters inParameters)
                throws ClientInitException, ConnectionException
        {
            return new ClientImpl(inParameters);
        }
    };
    /**
     * the <code>Client</code> object
     */
    private volatile Client mClient;
    /**
     * static instance
     */
    private volatile static ClientManager instance;
}
