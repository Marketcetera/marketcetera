package org.marketcetera.marketdata.bogus;

import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataModuleTestBase;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Tests {@link BogusFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class BogusFeedModuleTest
    extends MarketDataModuleTestBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getFactory()
     */
    @Override
    protected ModuleFactory getFactory()
    {
        return new BogusFeedModuleFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getInstanceURN()
     */
    @Override
    protected ModuleURN getInstanceURN()
    {
        return BogusFeedModuleFactory.INSTANCE_URN;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getExpectedCapabilities()
     */
    @Override
    protected Capability[] getExpectedCapabilities()
    {
        return new Capability[] { Capability.TOP_OF_BOOK };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getUnexpectedCapability()
     */
    @Override
    protected Capability getUnexpectedCapability()
    {
        return Capability.OHLC;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getProvider()
     */
    @Override
    protected String getProvider()
    {
        return BogusFeedModuleFactory.IDENTIFIER;
    }
}
