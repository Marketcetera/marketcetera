package org.marketcetera.marketdata.rpc.client;

import org.marketcetera.marketdata.MarketDataClientFactory;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates <code>MarketDataServiceRpcClient</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRpcClientFactory.java 17251 2016-09-08 23:18:29Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: MarketDataRpcClientFactory.java 17251 2016-09-08 23:18:29Z colin $")
public class MarketDataRpcClientFactory
        implements RpcClientFactory<MarketDataRpcClientParameters,MarketDataRpcClient>,MarketDataClientFactory<MarketDataRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public MarketDataRpcClient create(MarketDataRpcClientParameters inParameters)
    {
        return new MarketDataRpcClient(inParameters);
    }
}
