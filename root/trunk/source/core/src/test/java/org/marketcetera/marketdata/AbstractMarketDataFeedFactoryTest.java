package org.marketcetera.marketdata;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class AbstractMarketDataFeedFactoryTest
        extends TestCase
{
    /**
     * Create a new AbstractMarketDataFeedFactoryTest instance.
     *
     * @param inArg0
     */
    public AbstractMarketDataFeedFactoryTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        return MarketDataFeedTestBase.suite(AbstractMarketDataFeedFactoryTest.class);
    }

    public void testProperties()
        throws Exception
    {
        assertTrue(Arrays.equals(new String[0],
                                 mTestFactory.getAllowedPropertyKeys()));
    }
    
    private IMarketDataFeedFactory mTestFactory = new TestMarketDataFactory(); 
}
