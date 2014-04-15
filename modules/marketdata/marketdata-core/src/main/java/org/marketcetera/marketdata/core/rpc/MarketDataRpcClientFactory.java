package org.marketcetera.marketdata.core.rpc;

import org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Creates <code>MarketDataServiceRpcClient</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MarketDataRpcClientFactory
        implements MarketDataServiceClientFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int, org.marketcetera.util.ws.ContextClassProvider)
     */
    @Override
    public MarketDataRpcClient create(String inUsername,
                                      String inPassword,
                                      String inHostname,
                                      int inPort,
                                      ContextClassProvider inContextClassProvider)
    {
        return new MarketDataRpcClient(inUsername,
                                       inPassword,
                                       inHostname,
                                       inPort,
                                       inContextClassProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public MarketDataRpcClient create(String inUsername,
                                      String inPassword,
                                      String inHostname,
                                      int inPort)
    {
        return new MarketDataRpcClient(inUsername,
                                       inPassword,
                                       inHostname,
                                       inPort,
                                       null);
    }
}
