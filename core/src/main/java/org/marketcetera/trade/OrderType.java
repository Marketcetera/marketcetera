package org.marketcetera.trade;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Enumerations of various order types.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
     * A Market on Close order.
     */
    MarketOnClose(quickfix.field.OrdType.MARKET_ON_CLOSE),
    /**
     * Sentinel value for Order Types that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE),
    ForexLimit(quickfix.field.OrdType.FOREX_LIMIT),
    ForexMarket(quickfix.field.OrdType.FOREX_MARKET),
    ForexPreviouslyQuoted(quickfix.field.OrdType.FOREX_PREVIOUSLY_QUOTED),
    ForexSwap(quickfix.field.OrdType.FOREX_SWAP),
    Funari(quickfix.field.OrdType.FUNARI),
    LimitOnClose(quickfix.field.OrdType.LIMIT_ON_CLOSE),
    LimitOrBetter(quickfix.field.OrdType.LIMIT_OR_BETTER),
    LimitWithOrWithout(quickfix.field.OrdType.LIMIT_WITH_OR_WITHOUT),
    OnBasis(quickfix.field.OrdType.ON_BASIS),
    OnClose(quickfix.field.OrdType.ON_CLOSE),
    Pegged(quickfix.field.OrdType.PEGGED),
    PreviouslyIndicated(quickfix.field.OrdType.PREVIOUSLY_INDICATED),
    PreviouslyQuoted(quickfix.field.OrdType.PREVIOUSLY_QUOTED),
    Stop(quickfix.field.OrdType.STOP),
    StopLimit(quickfix.field.OrdType.STOP_LIMIT),
    WithOrWithout(quickfix.field.OrdType.WITH_OR_WITHOUT);

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
    public static OrderType getInstanceForFIXValue(char inValue) {
        OrderType ot = mFIXValueMap.get(inValue);
        return ot == null
                ? Unknown
                : ot;
    }
    /**
     * Indicate if the order type represents a market order (without a price).
     *
     * @return a <code>boolean</code> value
     */
    public boolean isMarketOrder()
    {
        return marketTypes.contains(this);
    }
    /**
     * Indicate if the order type represetns a stop order.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isStopOrder()
    {
        return stopTypes.contains(this);
    }
    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    private OrderType(char inFIXValue) {
        mFIXValue = inFIXValue;
    }
    /**
     * FIX value of the order type
     */
    private final char mFIXValue;
    /**
     * stores FIX values to order type values
     */
    private static final Map<Character,OrderType> mFIXValueMap;
    /**
     * market order types
     */
    private static final Set<OrderType> marketTypes = EnumSet.of(Market,MarketOnClose,ForexMarket,Stop);
    /**
     * stop order types
     */
    private static final Set<OrderType> stopTypes = EnumSet.of(Stop,StopLimit);
    /**
     * perform static initialization
     */
    static {
        Map<Character, OrderType> table = new HashMap<Character, OrderType>();
        for(OrderType ot: values()) {
            table.put(ot.getFIXValue(), ot);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}
