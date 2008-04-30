package org.marketcetera.marketdata;

import org.marketcetera.core.MarketceteraException;

/**
 * Returns an {@link IMarketDataFeed} instance.
 * 
 * <p>An {@link IMarketDataFeed} instance provides market data from a market data provider.
 * Callers can optionally determine the set of properties that the feed understands.
 * Setting properties may be optional or required depending on the implementation of the data feed.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public interface IMarketDataFeedFactory<F extends IMarketDataFeed,C extends IMarketDataFeedCredentials> 
{
    /**
     * Returns a data feed object that can resolve queries.
     *
     * @return a <code>F</code> value
     * @throws MarketceteraException if a feed object cannot be returned
     */
    public F getMarketDataFeed()
        throws MarketceteraException;
    /**
     * Returns a data feed object that can resolve queries.
     * 
     * <p>If non-null credentials are supplied, these credentials will be used
     * for queries submitted to this feed until new credentials are supplied
     * to a query.
     *
     * @param inCredentials a <code>C</code> value
     * @return a <code>F</code> value
     * @throws MarketceteraException if a feed object cannot be returned
     */
    public F getMarketDataFeed(C inCredentials)
        throws MarketceteraException;
	/**
     * Gets the data feed provider name. 
     *
     * @return a <code>String</code> value
	 */
    public String getProviderName();
}
