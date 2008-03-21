package org.marketcetera.marketdata;

import java.util.Map;

import org.apache.log4j.Logger;
import org.marketcetera.core.MarketceteraException;

/**
 * Returns an {@link IMarketDataFeed} instance.
 * 
 * <p>An {@link IMarketDataFeed} instance provides market data from a market data provider.
 * Callers can optionally determine the set of properties that the feed understands:
 * <pre>
 * MyMarketDataFeedFactory factory = MyMarketDataFeedFactory.getInstance();
 * String[] properties = factory.getAllAllowedPropertyKeys();
 * Map<String,Object> properties = new HashMap<String,Object>();
 * ...
 * MyMarketDataFeed feed = factory.getInstance(...);
 * </pre> 
 * Setting properties may be optional or required depending on the implementation of the data feed.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public interface IMarketDataFeedFactory 
{
    public IMarketDataFeed getMarketDataFeed(MarketDataFeedCredentials inCredentials)
        throws MarketceteraException;

    @Deprecated
    public IMarketDataFeed getInstance(String url, 
                                       String userName, 
                                       String password, 
                                       Map<String, Object> properties) 
        throws MarketceteraException;
    
	@Deprecated
    public IMarketDataFeed getInstance(String url, 
                                       String userName, 
                                       String password, 
                                       Map<String, Object> properties, 
                                       Logger logger) 
        throws MarketceteraException;

	/**
     * Gets the set of properties the {@link IMarketDataFeed} can understand. 
     *
     * @return a <code>String[]</code> value
	 */
    public String[] getAllowedPropertyKeys();
    
	/**
     * Gets the data feed provider name. 
     *
     * @return a <code>String</code> value
	 */
    public String getProviderName();
}
