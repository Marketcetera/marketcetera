package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/* $License$ */
/**
 * The base class for reports.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class ReportBaseImpl extends FIXMessageWrapper implements ReportBase {

    @Override
    public OrderID getOrderID() {
        return FIXUtil.getOrderID(getMessage());
    }

    @Override
    public OrderID getOriginalOrderID() {
        return FIXUtil.getOriginalOrderID(getMessage());
    }

    @Override
    public OrderStatus getOrderStatus() {
        return FIXUtil.getOrderStatus(getMessage());
    }

    @Override
    public String getText() {
        return FIXUtil.getText(getMessage());
    }

    @Override
    public DestinationID getDestinationID() {
        return mDestinationID;
    }
    /**
     * Creates an instance.
     *
     * @param inMessage the FIX Message.
     * @param inDestinationID the broker / destinationID from which this
     * FIX Message was received.
     */
    protected ReportBaseImpl(Message inMessage,
                             DestinationID inDestinationID) {
        super(inMessage);
        mDestinationID = inDestinationID;
    }

    private final DestinationID mDestinationID;
    private static final long serialVersionUID = 1L;
}
