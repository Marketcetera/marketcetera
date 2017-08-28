package org.marketcetera.trading.rpc;

import org.marketcetera.trade.client.TradingClient;
import org.marketcetera.trade.client.TradingClientFactory;

/* $License$ */

/**
 * Creates RPC {@link TradingClient} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradingRpcClientFactory
        implements TradingClientFactory<TradingRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public TradingRpcClient create(TradingRpcClientParameters inParameters)
    {
        return new TradingRpcClient(inParameters);
    }
}
