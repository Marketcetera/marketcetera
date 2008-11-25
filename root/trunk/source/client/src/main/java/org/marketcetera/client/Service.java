package org.marketcetera.client;

import javax.jws.WebService;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.ServiceBase;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * The application's web services.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@WebService
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Service
    extends ServiceBase
{
    /**
     * Returns the receiver's destination status to the client with
     * the given context.
     *
     * @param context The context.
     *
     * @return The status.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    DestinationsStatus getDestinationsStatus
        (ClientContext context)
        throws RemoteException;
}
