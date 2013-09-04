package org.marketcetera.client;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.misc.ClassVersion;


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
     * Create a new ClientManager instance.
     */
    public ClientManager()
    {
        instance = this;
    }
    /**
     * 
     *
     *
     * @return a <code>ClientManager</code> value
     * @throws IllegalArgumentException if the <code>ClientManager</code> has not yet been created
     */
    public synchronized static ClientManager getManagerInstance()
    {
        Validate.notNull(instance,
                         Messages.CLIENT_NOT_INITIALIZED.getText());
        return instance;
    }
    /**
     * Initializes the connection to the server.
     *
     * @param inParameters a <code>ClientParameters</code> value
     * @return a <code>Client</code> value used to connect to the server
     * @throws ConnectionException if there were errors connecting to the server.
     * @throws ClientInitException if an error occurred initializing the client
     * @throws IllegalArgumentException if the <code>ClientFactory</code> has not been set
     */
    public Client init(ClientParameters inParameters)
            throws ConnectionException, ClientInitException
    {
        Validate.notNull(mClientFactory,
                         Messages.CLIENT_NOT_INITIALIZED.getText());
        Client client = mClientFactory.getClient(inParameters);
        clients.put(inParameters.getParametersSpec(),
                    client);
        lastClientInstance = client;
        return client;
    }
    /**
     * Sets the <code>ClientFactory</code> to use to create the <code>Client</code>.
     *
     * @param inFactory a <code>ClientFactory</code> value
     * @throws ClientInitException if the <code>ClientFactory</code> is already initialized.
     */
    public void setClientFactory(ClientFactory inFactory)
            throws ClientInitException
    {
        if(mClientFactory != null) {
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
     * @deprecated
     */
    @Deprecated
    public Client getInstance()
            throws ClientInitException
    {
        if(isInitialized()) {
            return lastClientInstance;
        } else {
            throw new ClientInitException(Messages.CLIENT_NOT_INITIALIZED);
        }
    }
    /**
     * Returns the <code>Client</code> instance, if any, identified by the given <code>ClientParametersSpec</code>
     * after it has been initialized.
     * 
     * @param inParametersSpec a <code>ClientParametersSpec</code> value
     * @return a <code>Client</code> value or <code>null</code> if the <code>Client</code> has not yet been initialized
     */
    public Client getInstance(ClientParametersSpec inParametersSpec)
    {
        return clients.get(inParametersSpec);
    }
    /**
     * Returns true if the client is initialized, false if it's not.
     *
     * @return if the client is initialized.
     * @deprecated
     */
    @Deprecated
    public boolean isInitialized()
    {
        return lastClientInstance != null;
    }
    /**
     * the default <code>ClientFactory</code> to use to create the <code>Client</code> object 
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
     * 
     */
    private static ClientManager instance;
    /**
     * 
     */
    private final Map<ClientParametersSpec,Client> clients = new WeakHashMap<ClientParametersSpec,Client>();
    /**
     * 
     */
    @Deprecated
    private Client lastClientInstance;
}
