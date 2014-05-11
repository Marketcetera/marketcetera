package org.marketcetera.marketdata.core.manager;

import org.marketcetera.marketdata.core.MarketDataProvider;
import org.marketcetera.marketdata.core.ProviderStatus;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Tracks feed status of market data providers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataProviderRegistry.java 16422 2013-01-03 19:43:24Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id$")
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
