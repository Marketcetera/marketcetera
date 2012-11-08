package org.marketcetera.api.symbolresolver;

import org.junit.BeforeClass;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.symbolresolver.EquitySymbolResolver;

/* $License$ */

/**
 * Tests {@link EquitySymbolResolver}.
 *
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
