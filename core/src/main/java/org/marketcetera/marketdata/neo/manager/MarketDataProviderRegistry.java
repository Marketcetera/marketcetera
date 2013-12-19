package org.marketcetera.marketdata.neo.manager;

import org.marketcetera.marketdata.neo.ProviderStatus;
import org.marketcetera.marketdata.neo.provider.MarketDataProvider;

/* $License$ */

/**
 * Tracks feed status of market data providers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataProviderRegistry.java 16422 2013-01-03 19:43:24Z colin $
 * @since $Release$
 */
public interface MarketDataProviderRegistry
{
    /**
     * Set the status value for the given provider.
     *
     * @param inProvider a <code>MarketDataProvider</code> value
     * @param inStatus a <code>FeedStatus</code> value
     */
    public void setStatus(MarketDataProvider inProvider,
                          ProviderStatus inStatus);
}
