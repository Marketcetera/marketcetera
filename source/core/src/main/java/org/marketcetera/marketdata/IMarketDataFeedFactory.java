package org.marketcetera.marketdata;

import org.marketcetera.core.MarketceteraException;

/**
 * Constructs a {@link IMarketDataFeed} instance.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public interface IMarketDataFeedFactory 
{
	/**
     * Constructs an <code>IMarketDataFeed</code> instance.
     *  
     * @param inCredentials a <code>MarketDataFeedCredentials</code> object containing the information
     *   necessary to connect to the given feed
     * @return an <code>IMarketDataFeed</code> value
     * @throws MarketceteraException if an error occurs
	 */
    public IMarketDataFeed getMarketDataFeed(MarketDataFeedCredentials inCredentials) 
        throws FeedException;
    
	/**
     * Returns a list of property names that the <code>IMarketDataFeed</code> can understand.
     * 
     * <p>The names returned represent the properties that can be assigned a value to aid
     * in the construction of the data feed instance 
     * via {@link IMarketDataFeedFactory#getMarketDataFeed(MarketDataFeedCredentials)}.
     * The exact values returned will depend on the implemnetation.  They may be optional or
     * required depending on the particular subclass.
     *  
     * @return a <code>String[]</code> value
	 */
    public String[] getAllowedPropertyKeys();
    
	/**
     * Describes the data feed provider.
     *  
     * @return a <code>String</code> value
	 */
    public String getProviderName();
}
