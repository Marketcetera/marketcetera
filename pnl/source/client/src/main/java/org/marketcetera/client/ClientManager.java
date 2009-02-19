package org.marketcetera.client;

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
@ClassVersion("$Id$") //$NON-NLS-1$
public final class ClientManager {

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
            throws ConnectionException, ClientInitException {
        if (!isInitialized()) {
            mClientImpl = new ClientImpl(inParameter);
        } else {
            throw new ClientInitException(Messages.CLIENT_ALREADY_INITIALIZED);
        }
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
            return mClientImpl;
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
        return mClientImpl != null;
    }

    /**
     * Resets the client to the uninitialized state. This method is invoked
     * by the client implementation when it's {@link Client#close() closed}.
     * This method is not meant to be used by clients. 
     */
    synchronized static void reset() {
        mClientImpl = null;
    }

    /**
     * Do not allow any instances to be created.
     */
    private ClientManager() {
    }

    private volatile static ClientImpl mClientImpl;
}
