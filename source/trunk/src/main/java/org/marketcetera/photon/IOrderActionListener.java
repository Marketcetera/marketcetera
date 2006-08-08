package org.marketcetera.photon;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

/**
 * Interface for notifications involving FIX-encoded actions
 * on an order.  For example, new orders, cancels, cancel/replaces...
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public interface IOrderActionListener {
    /**
     * Indicates that an action was taken, by passing in the FIX message
     * associated with the action.
     * 
     * @param fixMessage the message indicating the action.
     */
    void orderActionTaken(Message fixMessage);
}
