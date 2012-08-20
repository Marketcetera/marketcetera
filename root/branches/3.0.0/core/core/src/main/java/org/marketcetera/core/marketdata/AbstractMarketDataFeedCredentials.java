package org.marketcetera.core.marketdata;

/**
 * Encapsulates the credentials necessary to authenticate a connection with an 
 * {@link MarketDataFeed} instance.
 * 
 * <p>Subclasses should override and add attributes and methods appropriate for
 * the data feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractMarketDataFeedCredentials.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
public abstract class AbstractMarketDataFeedCredentials
    implements MarketDataFeedCredentials
{
}
