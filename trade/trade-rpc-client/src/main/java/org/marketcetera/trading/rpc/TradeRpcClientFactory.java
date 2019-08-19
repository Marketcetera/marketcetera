package org.marketcetera.trading.rpc;

import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trade.client.TradeClientFactory;

/* $License$ */

/**
 * Creates RPC {@link TradeClient} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeRpcClientFactory
        implements TradeClientFactory<TradeRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public TradeRpcClient create(TradeRpcClientParameters inParameters)
    {
        return new TradeRpcClient(inParameters);
    }
}
