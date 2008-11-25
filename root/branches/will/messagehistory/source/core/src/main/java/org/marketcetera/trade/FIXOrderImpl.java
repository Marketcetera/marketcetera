package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/* $License$ */
/**
 * Message that wraps a FIX Message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class FIXOrderImpl extends FIXMessageWrapper implements FIXOrder {

    @Override
    public Message getMessage() {
        return mMessage;
    }

    @Override
    public SecurityType getSecurityType() {
        return FIXUtil.getSecurityType(getMessage());
    }

    @Override
    public DestinationID getDestinationID() {
        return mDestinationID;
    }

    @Override
    public void setDestinationID(DestinationID inDestinationID) {
        if(inDestinationID == null) {
            throw new NullPointerException();
        }
        mDestinationID = inDestinationID;
    }
    /**
     * Creates an instance.
     *
     * @param inMessage The FIX Message instance. Cannot be null.
     * @param inDestinationID the ID of the destination / broker to which
     * this order should be sent. Cannot be null.
     */
    FIXOrderImpl(Message inMessage, DestinationID inDestinationID) {
        super(inMessage);
        if(inMessage == null || inDestinationID == null) {
            throw new NullPointerException();
        }
        mMessage = inMessage;
        mDestinationID = inDestinationID;
    }

    private DestinationID mDestinationID;
    private final Message mMessage;
    private static final long serialVersionUID = 1L;
}
