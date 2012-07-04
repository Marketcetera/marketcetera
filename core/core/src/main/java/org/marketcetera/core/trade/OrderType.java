package org.marketcetera.core.trade;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Enumerations of various order types.
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderType.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public enum OrderType {
    /**
     * A Market Price order.
     */
    Market(quickfix.field.OrdType.MARKET),
    /**
     * A Limit order.
     */
    Limit(quickfix.field.OrdType.LIMIT),
    /**
     * Sentinel value for Order Types that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE);

    /**
     * The FIX char value for this instance.
     *
     * @return the FIX char value for this instance.
     */
    public char getFIXValue() {
        return mFIXValue;
    }

    /**
     * Returns the OrderType instance corresponding to the supplied FIX
     * char value.
     *
     * @param inValue the FIX char value.
     *
     * @return the corresponding OrderType instance.
     */
    static OrderType getInstanceForFIXValue(char inValue) {
        OrderType ot = mFIXValueMap.get(inValue);
        return ot == null
                ? Unknown
                : ot;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    private OrderType(char inFIXValue) {
        mFIXValue = inFIXValue;
    }
    private final char mFIXValue;
    private static final Map<Character, OrderType> mFIXValueMap;
    static {
        Map<Character, OrderType> table = new HashMap<Character, OrderType>();
        for(OrderType ot: values()) {
            table.put(ot.getFIXValue(), ot);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}
