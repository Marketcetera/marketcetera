package org.marketcetera.core.marketdata;

/* $License$ */

/**
 * Base implementation of {@link IMarketDataFeedFactory}.
 *
 * @version $Id: AbstractMarketDataFeedFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
public abstract class AbstractMarketDataFeedFactory<F extends MarketDataFeed<? extends MarketDataFeedToken,C>,
                                                    C extends MarketDataFeedCredentials>
        implements IMarketDataFeedFactory<F,C>
{
}
