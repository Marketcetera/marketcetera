package org.marketcetera.trade;

import org.marketcetera.core.attributes.ClassVersion;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/* $License$ */
/**
 * Indicates the type of Security.
 *
 * @author anshul@marketcetera.com
 * @version $Id: SecurityType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: SecurityType.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
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
     * 
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
