package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

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
