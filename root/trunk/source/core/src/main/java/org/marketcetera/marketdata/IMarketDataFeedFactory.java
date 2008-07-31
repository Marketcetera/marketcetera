package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;

/* $License$ */

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
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface IMarketDataFeedFactory<F extends IMarketDataFeed<? extends IMarketDataFeedToken<C>,C>,
                                        C extends IMarketDataFeedCredentials> 
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
     * Returns a data feed object that can resolve queries.
     * 
     * <p>If non-null credentials are supplied, these credentials will be used
     * for queries submitted to this feed until new credentials are supplied
     * to a query.
     *
     * @param inCredentials a <code>C</code> value
     * @return a <code>F</code> value
     * @throws CoreException if a feed object cannot be returned
     */
    public F getMarketDataFeed(C inCredentials)
        throws CoreException;
	/**
     * Gets the data feed provider name. 
     *
     * @return a <code>String</code> value
	 */
    public String getProviderName();
}
