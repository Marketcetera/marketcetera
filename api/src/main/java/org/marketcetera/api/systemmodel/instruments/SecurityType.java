package org.marketcetera.api.systemmodel.instruments;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* $License$ */
/**
 * Indicates the type of Security.
 *
 * @version $Id$
 * @since 1.0.0
 */
public enum SecurityType {
    /**
     * Sentinel value for Security Types that the system is not currently
     * aware of.
     */
    Unknown(""),  //$NON-NLS-1$
    /**
     * Common Stock.
     */
    CommonStock(quickfix.field.SecurityType.COMMON_STOCK),
    /**
     * Options
     */
    Option(quickfix.field.SecurityType.OPTION),
    /**
     * Future
     */
    Future(quickfix.field.SecurityType.FUTURE),
    /**
     * Convertible bond
     */
    ConvertibleBond(quickfix.field.SecurityType.CONVERTIBLE_BOND);
    /**
     * The FIX text value for this instance.
     *
     * @return the FIX text value for this instance.
     */
    public String getFIXValue() {
        return mFIXValue;
    }

    /**
     * Gets the SecurityType instance for the supplied FIX text value.
     *
     * @param inFIXValue the FIX text value.
     *
     * @return the corresponding SecurityType instnace.
     */
    public static SecurityType getInstanceForFIXValue(String inFIXValue) {
        if(inFIXValue == null) {
            return Unknown;
        }
        SecurityType st = mFIXValueMap.get(inFIXValue);
        return st == null
                ? Unknown
                : st;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX text value for this instance.
     */
    private SecurityType(String inFIXValue) {
        mFIXValue = inFIXValue;
    }
    private final String mFIXValue;
    private static final Map<String, SecurityType> mFIXValueMap;
    static {
        Map<String, SecurityType> table = new HashMap<String, SecurityType>();
        for(SecurityType st: values()) {
            table.put(st.getFIXValue(),st);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}
