package org.marketcetera.marketdata;

import java.util.Map;

import org.marketcetera.trade.SecurityType;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;

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
    CURRENCY,
    /**
     * convertible bond
     */
    CONVERTIBLE_BOND;
    /**
     * Gets the <code>AssetClass</code> value associated with the given <code>SecurityType</code>.
     *
     * @param inSecurityType a <code>SecurityType</code> value
     * @return an <code>AssetClass</code> value
     * @throws UnsupportedOperationException if the given <code>SecurityType</code> is not associated with an <code>AssetClass</code>
     */
    public static AssetClass getFor(SecurityType inSecurityType)
    {
        AssetClass value = values.get(inSecurityType);
        if(value == null) {
            throw new UnsupportedOperationException();
        }
        return value;
    }
    /**
     * Indicates if the asset class is an appropriate match for a request
     * by underlying symbols.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isValidForUnderlyingSymbols()
    {
        return this == OPTION || this == FUTURE;
    }
    /**
     * maps <code>SecurityType</code> values to <code>AssetClass</code> values for lookup
     */
    private static final Map<SecurityType,AssetClass> values = Maps.newHashMap();
    /**
     * performs one-time-only initialization of static values
     */
    static {
        values.put(SecurityType.CommonStock,EQUITY);
        values.put(SecurityType.Option,OPTION);
        values.put(SecurityType.Future,FUTURE);
        values.put(SecurityType.Currency,CURRENCY);
        values.put(SecurityType.ConvertibleBond,CONVERTIBLE_BOND);
    }
}