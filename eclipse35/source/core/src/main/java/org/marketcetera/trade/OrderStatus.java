package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/* $License$ */
/**
 * Order status values.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public enum OrderStatus {
    /**
     * Sentinel value for Order Status that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE),
    New(quickfix.field.OrdStatus.NEW),
    PartiallyFilled(quickfix.field.OrdStatus.PARTIALLY_FILLED),
    Filled(quickfix.field.OrdStatus.FILLED),
    DoneForDay(quickfix.field.OrdStatus.DONE_FOR_DAY),
    Canceled(quickfix.field.OrdStatus.CANCELED),
    Replaced(quickfix.field.OrdStatus.REPLACED),
    PendingCancel(quickfix.field.OrdStatus.PENDING_CANCEL),
    Stopped(quickfix.field.OrdStatus.STOPPED),
    Rejected(quickfix.field.OrdStatus.REJECTED),
    Suspended(quickfix.field.OrdStatus.SUSPENDED),
    PendingNew(quickfix.field.OrdStatus.PENDING_NEW),
    Calculated(quickfix.field.OrdStatus.CALCULATED),
    Expired(quickfix.field.OrdStatus.EXPIRED),
    AcceptedForBidding(quickfix.field.OrdStatus.ACCEPTED_FOR_BIDDING),
    PendingReplace(quickfix.field.OrdStatus.PENDING_REPLACE);

    /**
     * The FIX char value for this instance.
     *
     * @return the FIX char value for this instance.
     */
    public char getFIXValue() {
        return mFIXValue;
    }

    /**
     * Returns the OrderStatus instance corresponding to supplied FIX
     * field char value.
     *
     * @param inValue the FIX field value.
     *
     * @return the corresponding OrderStatus instance.
     */
    static OrderStatus getInstanceForFIXValue(char inValue) {
        OrderStatus status = mFIXValueTable.get(inValue);
        return status == null
                ? Unknown
                : status;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    private OrderStatus(char inFIXValue) {
        mFIXValue = inFIXValue;
    }
    private final char mFIXValue;
    private static final Map<Character, OrderStatus> mFIXValueTable;
    static {
        Map<Character, OrderStatus> table = new HashMap<Character, OrderStatus>();
        for(OrderStatus status: values()) {
            table.put(status.getFIXValue(), status);
        }
        mFIXValueTable = Collections.unmodifiableMap(table);
    }
}
