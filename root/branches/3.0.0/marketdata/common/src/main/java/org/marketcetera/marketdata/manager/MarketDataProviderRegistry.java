package org.marketcetera.marketdata.manager;

import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.provider.MarketDataProvider;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataProviderRegistry
{
    public void setStatus(MarketDataProvider inProvider,
                          FeedStatus inStatus)
            throws InterruptedException;
}
