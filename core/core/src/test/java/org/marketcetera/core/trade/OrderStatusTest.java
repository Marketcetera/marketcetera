package org.marketcetera.core.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;
import org.marketcetera.core.attributes.ClassVersion;
import quickfix.field.OrdStatus;

import static org.marketcetera.core.trade.OrderStatus.*;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.OrderStatus}
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderStatusTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderStatusTest.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public class OrderStatusTest extends FIXCharEnumTestBase<OrderStatus> {

    @Override
    protected OrderStatus getInstanceForFIXValue(Character inFIXValue) {
        return OrderStatus.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Character getFIXValue(OrderStatus e) {
        return e.getFIXValue();
    }

    @Override
    protected OrderStatus unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<OrderStatus> getValues() {
        return Arrays.asList(values());
    }
    @Override
    protected List<Pair<OrderStatus,Character>> knownValues()
    {
        List<Pair<OrderStatus,Character>> values = new ArrayList<Pair<OrderStatus,Character>>();
        values.add(new Pair<OrderStatus, Character>(New, OrdStatus.NEW));
        values.add(new Pair<OrderStatus, Character>(PartiallyFilled, OrdStatus.PARTIALLY_FILLED));
        values.add(new Pair<OrderStatus, Character>(Filled, OrdStatus.FILLED));
        values.add(new Pair<OrderStatus, Character>(DoneForDay, OrdStatus.DONE_FOR_DAY));
        values.add(new Pair<OrderStatus, Character>(Canceled, OrdStatus.CANCELED));
        values.add(new Pair<OrderStatus, Character>(PendingCancel, OrdStatus.PENDING_CANCEL));
        values.add(new Pair<OrderStatus, Character>(Stopped, OrdStatus.STOPPED));
        values.add(new Pair<OrderStatus, Character>(Rejected, OrdStatus.REJECTED));
        values.add(new Pair<OrderStatus, Character>(Suspended, OrdStatus.SUSPENDED));
        values.add(new Pair<OrderStatus, Character>(PendingNew, OrdStatus.PENDING_NEW));
        values.add(new Pair<OrderStatus, Character>(Calculated, OrdStatus.CALCULATED));
        values.add(new Pair<OrderStatus, Character>(Expired, OrdStatus.EXPIRED));
        values.add(new Pair<OrderStatus, Character>(AcceptedForBidding, OrdStatus.ACCEPTED_FOR_BIDDING));
        values.add(new Pair<OrderStatus, Character>(PendingReplace, OrdStatus.PENDING_REPLACE));
        values.add(new Pair<OrderStatus, Character>(Replaced, OrdStatus.REPLACED));
        return values;
    }
}
