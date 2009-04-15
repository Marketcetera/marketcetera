package org.marketcetera.marketdata.bogus;

import static org.marketcetera.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.marketdata.Capability.LEVEL_2;
import static org.marketcetera.marketdata.Capability.MARKET_STAT;
import static org.marketcetera.marketdata.Capability.OPEN_BOOK;
import static org.marketcetera.marketdata.Capability.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Capability.TOTAL_VIEW;
import static org.marketcetera.marketdata.Capability.UNKNOWN;

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
        return new Capability[] { TOP_OF_BOOK,LEVEL_2,OPEN_BOOK,TOTAL_VIEW,LATEST_TICK,MARKET_STAT };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getUnexpectedCapability()
     */
    @Override
    protected Capability getUnexpectedCapability()
    {
        return UNKNOWN;
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
