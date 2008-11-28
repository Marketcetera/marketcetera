package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/* $License$ */
/**
 * OrderCancelReject instances that wrap a FIX Message instance. This
 * class is public for the sake of JAXB and is not intended for
 * general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderCancelRejectImpl extends ReportBaseImpl
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

    /**
     * Creates an instance. This empty constructor is intended for use
     * by JAXB.
     */

    protected OrderCancelRejectImpl() {}

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
