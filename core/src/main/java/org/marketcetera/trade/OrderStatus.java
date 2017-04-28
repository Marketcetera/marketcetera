package org.marketcetera.trade;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;

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
     * Indicates if an order at this <code>OrderStatus</code> may be cancelled.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isCancellable()
    {
        return FIXMessageUtil.isCancellable(getFIXValue());
    }
    /**
     * Indicate if the order status is pending or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isPending()
    {
        return pendingOrderStatusValues.contains(this);
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
     * Returns the OrderStatus instance corresponding to supplied FIX
     * field char value.
     *
     * @param inValue the FIX field value.
     *
     * @return the corresponding OrderStatus instance.
     */
    public static OrderStatus getInstanceForFIXValue(char inValue) {
        OrderStatus status = mFIXValueTable.get(inValue);
        return status == null
                ? Unknown
                : status;
    }
    /**
     * Return the OrderStatus instance on the supplied FIX message.
     *
     * @param inMessage a <code>Message</code> value
     * @return an <code>OrderStatus</code> value
     * @throws IllegalArgumentException if the message does not contain an OrdStatus value
     */
    public static OrderStatus getInstanceForFIXMessage(Message inMessage)
    {
        try {
            return getInstanceForFIXValue(inMessage.getChar(quickfix.field.OrdStatus.FIELD));
        } catch (FieldNotFound e) {
            throw new IllegalArgumentException(e);
        }
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
    /**
     * holds status values that represent open orders
     */
    public static final Set<OrderStatus> openOrderStatuses;
    /**
     * holds status values that represent closed orders
     */
    public static final Set<OrderStatus> closedOrderStatuses;
    /**
     * status values that represent pending orders
     */
    public static final Set<OrderStatus> pendingOrderStatusValues = EnumSet.of(PendingCancel,PendingNew,PendingReplace);
    /**
     * Provides static initialization
     */
    static {
        Map<Character, OrderStatus> table = new HashMap<Character, OrderStatus>();
        Set<OrderStatus> openOrderStatusValues = new HashSet<>();
        Set<OrderStatus> closedOrderStatusValues = new HashSet<>();
        for(OrderStatus status: values()) {
            table.put(status.getFIXValue(), status);
            if(FIXMessageUtil.isCancellable(status.getFIXValue())) {
                openOrderStatusValues.add(status);
            } else {
                closedOrderStatusValues.add(status);
            }
        }
        closedOrderStatuses = Collections.unmodifiableSet(closedOrderStatusValues);
        openOrderStatuses = Collections.unmodifiableSet(openOrderStatusValues);
        mFIXValueTable = Collections.unmodifiableMap(table);
    }
}
