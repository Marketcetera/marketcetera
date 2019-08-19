package org.marketcetera.marketdata;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/* $License$ */

/**
 * Base class for market data provider <code>Module</code> tests that simulate data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public abstract class SimulatedMarketDataModuleTestBase
        extends MarketDataModuleTestBase
{
    @BeforeClass
    public static void setupOnce()
    {
        System.setProperty(AbstractMarketDataFeed.MARKETDATA_SIMULATION_KEY,
                           "true");
    }
    @AfterClass
    public static void teardownOnce()
    {
        System.setProperty(AbstractMarketDataFeed.MARKETDATA_SIMULATION_KEY,
                           "false");
    }
}
