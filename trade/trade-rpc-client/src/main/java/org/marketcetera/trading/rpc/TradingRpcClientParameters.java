package org.marketcetera.trading.rpc;

import org.marketcetera.rpc.client.RpcClientParameters;
import org.marketcetera.trade.client.TradeClientParameters;

/* $License$ */

/**
 * Provides parameters for the RPC trade client.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradingRpcClientParameters
        extends TradeClientParameters,RpcClientParameters
{
}
