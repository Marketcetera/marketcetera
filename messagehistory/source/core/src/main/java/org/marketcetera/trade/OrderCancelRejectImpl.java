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

    private static final long serialVersionUID = 1L;
}
