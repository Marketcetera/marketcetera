package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;

/* $License$ */
/**
 * Client's ID factory that generates unique IDs based on IDs given out by the
 * server. The generate IDs have the supplied prefix, if one is supplied,
 * followed by the ID provided by the server, followed by a client
 * generated number between 000-999. A request is made to the server to
 * request the next ID value, when the client ID value reaches 999.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ClientIDFactory implements IDFactory {
    /**
     * Creates an instance.
     *
     * @param inPrefix the prefix to use for all orderIDs.
     * @param inClientImpl the client impl to use to obtain orderIDs from
     * the server.
     */
    ClientIDFactory(String inPrefix, ClientImpl inClientImpl) {
        mPrefix = inPrefix == null
                ? ""   //$NON-NLS-1$
                : inPrefix;
        mClientImpl = inClientImpl;
    }

    @Override
    public synchronized String getNext() throws NoMoreIDsException {
        mClientID++;
        if(mClientID > MAX_CLIENT_ID) {
            getNextServer();
        }
        return String.format("%1$s%2$s%3$03d",  //$NON-NLS-1$
                mPrefix, mServerID, mClientID);
    }

    @Override
    public void init() throws NoMoreIDsException {
        getNextServer();
    }

    /**
     * Fetches the next orderID base from the server and initializes, the
     * client portion of the ID back to zero.
     *
     * @throws NoMoreIDsException if the ID couldn't be fetched from the server.
     */
    private void getNextServer() throws NoMoreIDsException {
        try {
            mServerID = mClientImpl.getNextServerID();
            mClientID = 0;
        } catch (RemoteException e) {
            Messages.LOG_UNABLE_FETCH_ID_SERVER.error(this,e);
            throw new NoMoreIDsException(e, Messages.UNABLE_FETCH_ID_SERVER);
        }
    }
    private String mServerID;
    private short mClientID = 0;
    private final String mPrefix;
    private final ClientImpl mClientImpl;
    static final short MAX_CLIENT_ID = 999;
}
