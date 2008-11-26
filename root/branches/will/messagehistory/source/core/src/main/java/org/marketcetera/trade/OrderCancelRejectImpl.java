package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/* $License$ */
/**
 * OrderCancelReject instances that wrap a FIX Message instance.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class OrderCancelRejectImpl extends ReportBaseImpl
        implements OrderCancelReject {
    /**
     * Creates an instance.
     *
     * @param inMessage The FIX Message instance.
     * @param inDestinationID the broker / destinationID from which this
     * message was received.
     */
    OrderCancelRejectImpl(Message inMessage, DestinationID inDestinationID) {
        super(inMessage, inDestinationID);
    }

    @Override
    public String toString() {
        return Messages.ORDER_CANCEL_REJECT_TO_STRING.getText(
                String.valueOf(getDestinationID()),
                String.valueOf(getOrderID()),
                String.valueOf(getOrderStatus()),
                String.valueOf(getOriginalOrderID()),
                String.valueOf(getSendingTime()),
                String.valueOf(getText()),
                String.valueOf(getDestinationOrderID()),
                String.valueOf(getMessage())
        );
    }

    private static final long serialVersionUID = 1L;
}
