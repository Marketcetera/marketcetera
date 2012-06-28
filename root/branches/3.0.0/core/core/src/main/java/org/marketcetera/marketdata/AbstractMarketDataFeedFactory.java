package org.marketcetera.marketdata;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Base implementation of {@link IMarketDataFeedFactory}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractMarketDataFeedFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
@ClassVersion("$Id: AbstractMarketDataFeedFactory.java 16063 2012-01-31 18:21:55Z colin $")
public abstract class AbstractMarketDataFeedFactory<F extends MarketDataFeed<? extends MarketDataFeedToken,C>,
                                                    C extends MarketDataFeedCredentials>
        implements IMarketDataFeedFactory<F,C>
{
}
