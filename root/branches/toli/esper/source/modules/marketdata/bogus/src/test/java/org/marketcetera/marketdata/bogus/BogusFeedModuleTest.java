package org.marketcetera.marketdata.bogus;

import org.marketcetera.marketdata.MarketDataModuleTestBase;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Tests {@link BogusFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BogusFeedModuleTest
    extends MarketDataModuleTestBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getFactory()
     */
    @Override
    protected ModuleFactory<? extends Module> getFactory()
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
}
