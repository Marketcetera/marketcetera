package org.marketcetera.marketdata.marketcetera;

import org.junit.Test;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.SimulatedMarketDataModuleTestBase;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ConfigurationProviderTest.MockConfigurationProvider;

/* $License$ */

/**
 * Tests {@link MarketceteraFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MarketceteraFeedModuleTest
    extends SimulatedMarketDataModuleTestBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#populateConfigurationProvider(org.marketcetera.module.ConfigurationProviderTest.MockConfigurationProvider)
     */
    @Override
    protected void populateConfigurationProvider(MockConfigurationProvider inProvider)
    {
        inProvider.addValue(MarketceteraFeedModuleFactory.INSTANCE_URN,
                            "URL",
                            "FIX.4.4://exchange.marketcetera.com:7004");
        inProvider.addValue(MarketceteraFeedModuleFactory.INSTANCE_URN,
                            "SenderCompID",
                            "sender");
        inProvider.addValue(MarketceteraFeedModuleFactory.INSTANCE_URN,
                            "TargetCompID",
                            "MRKT-" + System.nanoTime());
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getFactory()
     */
    @Override
    protected ModuleFactory getFactory()
    {
        return new MarketceteraFeedModuleFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getInstanceURN()
     */
    @Override
    protected ModuleURN getInstanceURN()
    {
        return MarketceteraFeedModuleFactory.INSTANCE_URN;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getExpectedCapabilities()
     */
    @Override
    protected Capability[] getExpectedCapabilities()
    {
        return new Capability[] { Capability.TOP_OF_BOOK, Capability.LATEST_TICK };
    }
    // TODO these tests are shimmed in until I can figure out how to simulate data in data feeds
    @Test
    public void dataRequestFromString()
        throws Exception
    {
    }
    @Test
    public void dataRequestProducesData()
        throws Exception
    {
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getProvider()
     */
    @Override
    protected String getProvider()
    {
        return MarketceteraFeedModuleFactory.IDENTIFIER;
    }
}
