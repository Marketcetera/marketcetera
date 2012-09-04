package org.marketcetera.core.marketdata;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.core.marketdata.AssetClass.*;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.marketdata.AssetClass}.
 *
 * @version $Id: AssetClassTest.java 82329 2012-04-10 16:28:13Z colin $
 * @since 2.1.0
 */
public class AssetClassTest
{
    /**
     * Tests {@link org.marketcetera.core.marketdata.AssetClass#isValidForUnderlyingSymbols()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validForUnderlying()
            throws Exception
    {
        assertTrue("New asset class added, modify the unit tests accordingly",
                   AssetClass.values().length == 3);
        assertFalse(EQUITY.isValidForUnderlyingSymbols());
        assertTrue(OPTION.isValidForUnderlyingSymbols());
        assertTrue(FUTURE.isValidForUnderlyingSymbols());
    }
}
