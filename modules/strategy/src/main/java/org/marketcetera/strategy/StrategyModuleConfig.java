package org.marketcetera.strategy;

/* $License$ */

/**
 * Provides configuration for the {@link StrategyModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyModuleConfig
{
    /**
     * Get the marketDataRequestProxy value.
     *
     * @return a <code>String</code> value
     */
    public String getMarketDataRequestProxy()
    {
        return marketDataRequestProxy;
    }
    /**
     * Sets the marketDataRequestProxy value.
     *
     * @param inMarketDataRequestProxy a <code>String</code> value
     */
    public void setMarketDataRequestProxy(String inMarketDataRequestProxy)
    {
        marketDataRequestProxy = inMarketDataRequestProxy;
    }
    /**
     * indicates whether to use a proxy module or not when executing market data requests
     */
    private String marketDataRequestProxy;
}
