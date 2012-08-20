package org.marketcetera.core.marketdata;

import org.marketcetera.core.CoreException;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Returns an {@link MarketDataFeed} instance.
 * 
 * <p>An {@link MarketDataFeed} instance provides market data from a market data provider.
 * Callers can optionally determine the set of properties that the feed understands.
 * Setting properties may be optional or required depending on the implementation of the data feed.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: IMarketDataFeedFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
@ClassVersion("$Id: IMarketDataFeedFactory.java 16063 2012-01-31 18:21:55Z colin $")
public interface IMarketDataFeedFactory<F extends MarketDataFeed<? extends MarketDataFeedToken,C>,
                                        C extends MarketDataFeedCredentials> 
{
    /**
     * Returns a data feed object that can resolve queries.
     *
     * @return a <code>F</code> value
     * @throws CoreException if a feed object cannot be returned
     */
    public F getMarketDataFeed()
        throws CoreException;
	/**
     * Gets the data feed provider name. 
     *
     * @return a <code>String</code> value
	 */
    public String getProviderName();
}
