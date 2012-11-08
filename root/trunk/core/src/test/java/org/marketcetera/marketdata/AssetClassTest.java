package org.marketcetera.marketdata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.AssetClass.EQUITY;
import static org.marketcetera.marketdata.AssetClass.FUTURE;
import static org.marketcetera.marketdata.AssetClass.OPTION;
import static org.marketcetera.marketdata.AssetClass.CURRENCY;

import org.junit.Test;

/* $License$ */

/**
 * Tests {@link AssetClass}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
public class AssetClassTest
{
    /**
     * Tests {@link AssetClass#isValidForUnderlyingSymbols()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validForUnderlying()
            throws Exception
    {
        assertTrue("New asset class added, modify the unit tests accordingly",
                   AssetClass.values().length == 4);
        assertFalse(EQUITY.isValidForUnderlyingSymbols());
        assertTrue(OPTION.isValidForUnderlyingSymbols());
        assertTrue(FUTURE.isValidForUnderlyingSymbols());
        assertFalse(CURRENCY.isValidForUnderlyingSymbols());
    }
}
