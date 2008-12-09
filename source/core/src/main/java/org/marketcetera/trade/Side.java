package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Enumeration of Sides of an order.
*
* @author anshul@marketcetera.com
* @version $Id$
* @since 1.0.0
*/
@ClassVersion("$Id$")
public enum Side {
    /**
     * Sentinel value for Side that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE),
    /**
     * A Buy Order.
     */
    Buy(quickfix.field.Side.BUY),
    /**
     * A Sell Order.
     */
    Sell(quickfix.field.Side.SELL),
    /**
     * A Sell Short Order.
     */
    SellShort(quickfix.field.Side.SELL_SHORT),
    /**
     * A Sell Short Exempt Order.
     */
    SellShortExempt(quickfix.field.Side.SELL_SHORT_EXEMPT);

    /**
     * Gets the Side instance.
     *
     * @param inValue the FIX char value.
     *
     * @return the Side instance.
     */
    static Side getInstanceForFIXValue(char inValue) {
        Side s = mFIXValueMap.get(inValue);
        return s == null
                ? Unknown
                : s;
    }
    /**
     * The FIX char value for this instance.
     *
     * @return the FIX char value for this instance.
     */
    public char getFIXValue() {
        return mFIXValue;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    Side(char inFIXValue) {
        mFIXValue = inFIXValue;
    }

    private final char mFIXValue;
    private static final Map<Character, Side> mFIXValueMap;
    static {
        Map<Character, Side> table = new HashMap<Character, Side>();
        for(Side s:values()) {
            table.put(s.getFIXValue(),s);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}
