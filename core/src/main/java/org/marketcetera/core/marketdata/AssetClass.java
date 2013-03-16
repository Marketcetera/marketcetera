package org.marketcetera.core.marketdata;

/**
 * The asset class for market data requests.
 *
 * @version $Id: AssetClass.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
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
    FUTURE;
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