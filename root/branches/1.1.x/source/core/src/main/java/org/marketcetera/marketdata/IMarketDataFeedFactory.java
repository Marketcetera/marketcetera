package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;

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
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
