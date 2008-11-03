package org.marketcetera.marketdata;

import org.marketcetera.core.MarketceteraTestSuite;

/**
 * Test suite for Market Data Feed Tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public class MarketDataFeedTestSuite
    extends MarketceteraTestSuite
{

    /**
     * Create a new <code>MarketDataFeedTestSuite</code> instance.
     *
     */
    public MarketDataFeedTestSuite()
    {
    }

    /**
     * Create a new <code>MarketDataFeedTestSuite</code> instance.
     *
     * @param inClass
     */
    public MarketDataFeedTestSuite(Class<?> inClass)
    {
        super(inClass);

    }

    public static DataRequest generateDataRequest() 
        throws FeedException
    {
        return MarketDataRequest.newFullBookRequest("GOOG",
                                                    "MSFT",
                                                    "YGZ9");
    }    
}
