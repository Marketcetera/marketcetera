package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.OrderStatus.*;
import org.marketcetera.core.Pair;
import java.util.Arrays;
import java.util.List;

import quickfix.field.OrdStatus;

/* $License$ */
/**
 * Tests {@link OrderStatus}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    protected List<Pair<OrderStatus,Character>> knownValues() {
        return Arrays.asList(
                new Pair<OrderStatus, Character>(New, OrdStatus.NEW),
                new Pair<OrderStatus, Character>(PartiallyFilled, OrdStatus.PARTIALLY_FILLED),
                new Pair<OrderStatus, Character>(Filled, OrdStatus.FILLED),
                new Pair<OrderStatus, Character>(DoneForDay, OrdStatus.DONE_FOR_DAY),
                new Pair<OrderStatus, Character>(Canceled, OrdStatus.CANCELED),
                new Pair<OrderStatus, Character>(PendingCancel, OrdStatus.PENDING_CANCEL),
                new Pair<OrderStatus, Character>(Stopped, OrdStatus.STOPPED),
                new Pair<OrderStatus, Character>(Rejected, OrdStatus.REJECTED),
                new Pair<OrderStatus, Character>(Suspended, OrdStatus.SUSPENDED),
                new Pair<OrderStatus, Character>(PendingNew, OrdStatus.PENDING_NEW),
                new Pair<OrderStatus, Character>(Calculated, OrdStatus.CALCULATED),
                new Pair<OrderStatus, Character>(Expired, OrdStatus.EXPIRED),
                new Pair<OrderStatus, Character>(AcceptedForBidding, OrdStatus.ACCEPTED_FOR_BIDDING),
                new Pair<OrderStatus, Character>(PendingReplace, OrdStatus.PENDING_REPLACE),
                new Pair<OrderStatus, Character>(Replaced, OrdStatus.REPLACED)
        );
    }
}