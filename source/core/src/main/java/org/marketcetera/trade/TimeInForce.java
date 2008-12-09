package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/* $License$ */
/**
 * The time in force for an Order.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public enum TimeInForce {
    /**
     * Sentinel value for Time In Force that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE),
    /**
     * Order valid for the day.
     */
    Day(quickfix.field.TimeInForce.DAY),
    /**
     * Order valid until cancelled.
     */
    GoodTillCancel(quickfix.field.TimeInForce.GOOD_TILL_CANCEL),
    /**
     * At market opening.
     */
    AtTheOpening(quickfix.field.TimeInForce.AT_THE_OPENING),
    /**
     * Immediate order execution or cancel.
     */
    ImmediateOrCancel(quickfix.field.TimeInForce.IMMEDIATE_OR_CANCEL),

    /**
     * Fill or Kill.
     */
    FillOrKill(quickfix.field.TimeInForce.FILL_OR_KILL),

    /**
     * At market close.
     */
    AtTheClose(quickfix.field.TimeInForce.AT_THE_CLOSE);

    /**
     * The FIX char value for this instance.
     *
     * @return the FIX char value for this instance.
     */
    char getFIXValue() {
        return mFIXValue;
    }
    /**
     * Gets the TimeInForce instance.
     *
     * @param inValue the FIX char value.
     *
     * @return the TimeInForce instance.
     */
    static TimeInForce getInstanceForFIXValue(char inValue) {
        TimeInForce tif = mFIXValueMap.get(inValue);
        return tif == null
                ? Unknown
                : tif;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    private TimeInForce(char inFIXValue) {
        mFIXValue = inFIXValue;
    }

    private final char mFIXValue;
    private static final Map<Character, TimeInForce> mFIXValueMap;
    static {
        Map<Character, TimeInForce> table = new HashMap<Character, TimeInForce>();
        for(TimeInForce tif:values()) {
            table.put(tif.getFIXValue(),tif);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}
