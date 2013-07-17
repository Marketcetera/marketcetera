package org.marketcetera.marketdata;

import org.marketcetera.util.misc.ClassVersion;

/**
 * The asset class for market data requests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum AssetClass
{
    /**
     * equities
     */
    EQUITY,
    /**
     * options
     */
    OPTION,
    /**
     * futures
     */
    FUTURE,
    /**
     * currency
     */
    CURRENCY;
    /**
     * Indicates if the asset class is an appropriate match for a request
     * by underlying symbols.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isValidForUnderlyingSymbols()
    {
        return this == OPTION ||
               this == FUTURE;
    }
}