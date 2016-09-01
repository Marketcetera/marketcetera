package org.marketcetera.tradingclient.client;

import org.marketcetera.tradingclient.TradingClient;

/* $License$ */

/**
 * Creates a <code>TradingClient</code> object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradingClientFactory<ParameterClazz>
{
    /**
     * Create a trading client with the given parameters.
     *
     * @param inParameters a <code>ParameterClazz</code> value
     * @return a <code>TradingClient</code> value
     */
    TradingClient create(ParameterClazz inParameters);
}
