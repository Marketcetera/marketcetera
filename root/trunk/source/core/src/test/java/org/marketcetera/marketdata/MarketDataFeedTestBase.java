package org.marketcetera.marketdata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Base class for Market Data Feed tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class MarketDataFeedTestBase
    extends TestCase
{
    private static MarketDataFeedTestSuite sSuite;
    
    /**
     * Create a new <code>MarketDataFeedTestBase</code> instance.
     *
     * @param inArg0
     */
    public MarketDataFeedTestBase(String inArg0)
    {
        super(inArg0);
    }

    protected static TestSuite suite(Class inClass) 
    {
        sSuite = new MarketDataFeedTestSuite(inClass);
        return sSuite;
    }
    
    protected static Test suite() 
    {
        sSuite = new MarketDataFeedTestSuite();
        return sSuite;
    }
    
    protected MarketDataFeedTestSuite getTestSuite()
    {
        return sSuite;
    }    
}
