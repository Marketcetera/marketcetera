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
    public SecurityType getSecurityType() {
        return FIXUtil.getSecurityType(getMessage());
    }

    @Override
    public BrokerID getBrokerID() {
        return mBrokerID;
    }

    @Override
    public void setBrokerID(BrokerID inBrokerID) {
        if(inBrokerID == null) {
            throw new NullPointerException();
        }
        mBrokerID = inBrokerID;
    }
    /**
     * Creates an instance.
     *
     * @param inMessage The FIX Message instance. Cannot be null.
     * @param inBrokerID the ID of the broker to which
     * this order should be sent. Cannot be null.
     */
    FIXOrderImpl(Message inMessage, BrokerID inBrokerID) {
        super(inMessage);
        if(inMessage == null || inBrokerID == null) {
            throw new NullPointerException();
        }
        mBrokerID = inBrokerID;
    }

    @Override
    public String toString() {
        return Messages.FIX_ORDER_TO_STRING.getText(
                getBrokerID().getValue(),
                String.valueOf(getMessage()));
    }

    private BrokerID mBrokerID;
    private static final long serialVersionUID = 1L;
}
