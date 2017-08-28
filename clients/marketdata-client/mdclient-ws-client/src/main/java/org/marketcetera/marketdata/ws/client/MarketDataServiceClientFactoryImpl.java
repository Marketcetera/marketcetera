package org.marketcetera.marketdata.ws.client;

import org.marketcetera.mdclient.MarketDataClientFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates a client to connect to Market Data Web Services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataServiceClientFactoryImpl.java 17251 2016-09-08 23:18:29Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: MarketDataServiceClientFactoryImpl.java 17251 2016-09-08 23:18:29Z colin $")
public class MarketDataServiceClientFactoryImpl
        implements MarketDataClientFactory<MarketDataWsClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.mdclient.MarketDataClientFactory#create(org.marketcetera.core.ClientParameters)
     */
    @Override
    public MarketDataServiceClientImpl create(MarketDataWsClientParameters inParameters)
    {
        MarketDataServiceClientImpl client = new MarketDataServiceClientImpl(inParameters);
        return client;
    }
}
