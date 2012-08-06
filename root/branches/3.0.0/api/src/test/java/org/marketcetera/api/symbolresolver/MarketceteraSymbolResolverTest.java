package org.marketcetera.api.symbolresolver;

import org.junit.BeforeClass;
import org.marketcetera.api.symbolresolver.impl.EquitySymbolResolver;
import org.marketcetera.core.LoggerConfiguration;

/* $License$ */

/**
 * Tests {@link EquitySymbolResolver}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketceteraSymbolResolverTest
{
    /**
     * Run once before all unit tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
}
