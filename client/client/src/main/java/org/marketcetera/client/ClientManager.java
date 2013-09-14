package org.marketcetera.client;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Abstraction that manages the initialization of the Client and provides
 * an easy way to get to its singleton instance.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public class ClientManager
{
    /**
     * Create a new <code>ClientManager</code> instance.
     * 
     * @throws IllegalArgumentException if an instance has already been created
     */
    public ClientManager()
    {
        synchronized(ClientManager.class) {
            Validate.isTrue(instance == null,
                            Messages.CLIENT_ALREADY_INITIALIZED.getText());
            instance = this;
        }
    }
    /**
     * Gets the singleton <code>ClientManager</code> instance.
     *
     * @return a <code>ClientManager</code> value
     * @throws IllegalArgumentException if the <code>ClientManager</code> has not yet been created
     */
    public synchronized static ClientManager getManagerInstance()
    {
        synchronized(ClientManager.class) {
            Validate.notNull(instance,
                             Messages.CLIENT_NOT_INITIALIZED.getText());
            return instance;
        }
    }
    /**
     * Initializes and opens a connection to the server.
     * 
     * <p>If the given parameters refer to an existing, open connection, the existing
     * connection will be returned instead of creating a new one.
     *
     * @param inParameters a <code>ClientParameters</code> value
     * @return a <code>Client</code> value used to connect to the server
     * @throws ConnectionException if there were errors connecting to the server.
     * @throws ClientInitException if an error occurred initializing the client
     */
    public Client init(ClientParameters inParameters)
            throws ConnectionException, ClientInitException
    {
        Lock initLock = clientsLock.writeLock();
        try {
            initLock.lockInterruptibly();
            Client client = inParameters == null ? null : clients.get(inParameters.getParametersSpec());
            if(client != null) {
                return client;
            }
            if(clientFactory == null) {
                throw new ClientInitException(Messages.NO_CLIENT_FACTORY);
            }
            client = clientFactory.getClient(inParameters,
                                             lifecycleManager);
            if(inParameters != null) {
                clients.put(inParameters.getParametersSpec(),
                            client);
            }
            lastClientInstance = client;
            return client;
        } catch (InterruptedException e) {
            throw new ClientInitException(e);
        } finally {
            initLock.unlock();
        }
    }
    /**
     * Sets the <code>ClientFactory</code> to use to create the <code>Client</code>.
     *
     * @param inFactory a <code>ClientFactory</code> value
     * @throws ClientInitException if the client factory cannot be set
     */
    public void setClientFactory(ClientFactory inFactory)
            throws ClientInitException
    {
        Lock setLock = clientsLock.writeLock();
        try {
            setLock.lockInterruptibly();
            clientFactory = inFactory;
        } catch (InterruptedException e) {
            throw new ClientInitException(e);
        } finally {
            setLock.unlock();
        }
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
        Lock getLock = clientsLock.readLock();
        try {
            getLock.lockInterruptibly();
            return clients.get(inParametersSpec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            getLock.unlock();
        }
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
     * allows client objects to notify the manager upon changes in their lifecycle
     */
    private final ClientLifecycleManager lifecycleManager = new ClientLifecycleManager()
    {
        @Override
        public void release(final Client inClient)
        {
            Lock getLock = clientsLock.writeLock();
            try {
                getLock.lockInterruptibly();
                clients.remove(inClient.getParameters().getParametersSpec());
            } catch (InterruptedException e) {
                SLF4JLoggerProxy.warn(ClientManager.class,
                                      e);
            } finally {
                getLock.unlock();
            }
        }
    };
    /**
     * the default <code>ClientFactory</code> to use to create the <code>Client</code> object 
     */
    @GuardedBy("clientsLock")
    private volatile ClientFactory clientFactory = new JmsClientFactory();
    /**
     * controls access to {@link #clientFactory}
     */
    private final ReadWriteLock clientsLock = new ReentrantReadWriteLock();
    /**
     * singleton instance
     */
    @GuardedBy("ClientManager")
    private static ClientManager instance;
    /**
     * tracks clients by parameter spec, which uniquely identifies the client
     */
    @GuardedBy("clientsLock")
    private final Map<ClientParametersSpec,Client> clients = new WeakHashMap<ClientParametersSpec,Client>();
    /**
     * most recently initialized client instance
     */
    @Deprecated
    private volatile Client lastClientInstance;
}
