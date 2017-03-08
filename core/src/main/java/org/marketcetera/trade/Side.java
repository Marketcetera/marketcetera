package org.marketcetera.trade;

import java.util.*;

import org.marketcetera.util.misc.ClassVersion;

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
    SellShortExempt(quickfix.field.Side.SELL_SHORT_EXEMPT),
    /**
     * A Buy Minus Order
     */
    BuyMinus(quickfix.field.Side.BUY_MINUS),
    /**
     * A Cross Order
     */
    Cross(quickfix.field.Side.CROSS),
    /**
     * A Cross Short Order
     */
    CrossShort(quickfix.field.Side.CROSS_SHORT),
    /**
     * A Sell Plus Order
     */
    SellPlus(quickfix.field.Side.SELL_PLUS),
    /**
     * An Undisclosed Order
     */
    Undisclosed(quickfix.field.Side.UNDISCLOSED);
    /**
     * Indicates if this is a buy side.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isBuy()
    {
        return BUY_SIDES.contains(this);
    }
    /**
     * Indicates if this is a sell side.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isSell()
    {
        return SELL_SIDES.contains(this);
    }
    /**
     * Gets the Side instance.
     *
     * @param inValue the FIX char value.
     *
     * @return the Side instance.
     */
    public static Side getInstanceForFIXValue(char inValue) {
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
    private Side(char inFIXValue) {
        mFIXValue = inFIXValue;
    }
    /**
     * FIX char of this value
     */
    private final char mFIXValue;
    /**
     * sell side values
     */
    public static final Set<Side> SELL_SIDES = EnumSet.of(Sell,SellShort,SellShortExempt,SellPlus);
    /**
     * buy side values
     */
    public static final Set<Side> BUY_SIDES = EnumSet.of(Buy,BuyMinus);
    /**
     * side values by FIX value
     */
    private static final Map<Character,Side> mFIXValueMap;
    static {
        Map<Character, Side> table = new HashMap<Character, Side>();
        for(Side s:values()) {
            table.put(s.getFIXValue(),s);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}
