package org.marketcetera.core.trade;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import quickfix.field.ExecType;

/* $License$ */
/**
 * Specific execution report status.
 *
 * @version $Id: ExecutionType.java 16326 2012-10-26 20:44:46Z colin $
 * @since 1.0.0
 */
public enum ExecutionType {
    /**
     * Sentinel value for Execution Type that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE),
    New(ExecType.NEW),
    PartialFill(ExecType.PARTIAL_FILL),
    Fill(ExecType.FILL),
    DoneForDay(ExecType.DONE_FOR_DAY),
    Canceled(ExecType.CANCELED),
    Replace(ExecType.REPLACE),
    PendingCancel(ExecType.PENDING_CANCEL),
    Stopped(ExecType.STOPPED),
    Rejected(ExecType.REJECTED),
    Suspended(ExecType.SUSPENDED),
    PendingNew(ExecType.PENDING_NEW),
    Calculated(ExecType.CALCULATED),
    Expired(ExecType.EXPIRED),
    Restated(ExecType.RESTATED),
    PendingReplace(ExecType.PENDING_REPLACE),
    Trade(ExecType.TRADE),
    TradeCorrect(ExecType.TRADE_CORRECT),
    TradeCancel(ExecType.TRADE_CANCEL),
    OrderStatus(ExecType.ORDER_STATUS);

    /**
     * The FIX char value for this instance.
     *
     * @return FIX char value for this instance.
     */
    public char getFIXValue() {
        return mFIXValue;
    }

    /**
     * Returns the ExecutionType instance given the FIX char value.
     *
     * @param inValue the FIX char value.
     *
     * @return the ExecutionType instance.
     */
    public static ExecutionType getInstanceForFIXValue(char inValue) {
        ExecutionType type = mFIXValueMap.get(inValue);
        return type == null
                ? Unknown
                : type;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    private ExecutionType(char inFIXValue) {
        mFIXValue = inFIXValue;
    }
    private final char mFIXValue;
    private static final Map<Character, ExecutionType> mFIXValueMap;
    static {
        //Initialize the lookup table.
        Map<Character,ExecutionType> table = new HashMap<Character, ExecutionType>();
        for(ExecutionType et:values()) {
            table.put(et.getFIXValue(), et);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}
