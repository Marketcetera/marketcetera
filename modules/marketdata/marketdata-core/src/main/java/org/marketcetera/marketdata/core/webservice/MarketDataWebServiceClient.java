package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataWebServiceClient
        extends Lifecycle
{
    long request(MarketDataRequest inRequest)
            throws RemoteException;
}
