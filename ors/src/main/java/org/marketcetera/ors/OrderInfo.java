package org.marketcetera.ors;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 * An entry for order information in a {@link OrderInfoCache} cache.
 *
 * @author tlerios@marketcetera.com
 * @since 2.1.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface OrderInfo
{

    /**
     * Returns the ID of the receiver's order.
     *
     * @return The ID.
     */

    public OrderID getOrderID();

    /**
     * Returns the parent order ID of the receiver's order.
     *
     * @return The ID. It may be null if the receiver's order is a
     * chain root.
     */

    public OrderID getOrigOrderID();

    /**
     * Returns the actor ID of the receiver's order.
     *
     * @return The ID.
     */

    public UserID getActorID();

    /**
     * Checks whether the viewer ID of the receiver's order has been
     * set. Specifically, false indicates unknown-at-present (to be
     * lazily researched when needed) and, until that time, {@link
     * #getViewerID()} should not be used; true indicates that {@link
     * #getViewerID()} can be used.
     *
     * @return True if so.
     */

    public boolean isViewerIDSet();

    /**
     * Returns the viewer ID of the receiver's order. It should only
     * be called after a call to {@link #setViewerID(UserID)} (which
     * may take place implicitly, in the constructor); accordingly, it
     * is best to guard calls to this method with a call to {@link
     * #isViewerIDSet()}.
     *
     * @return The ID. It may be null if we could not deduce a viewer.
     */

    public UserID getViewerID();

    /**
     * Sets the viewer ID of the receiver's order to the given one.
     *
     * @param viewerID The ID. It may be null if we could not deduce a
     * viewer.
     */

    public void setViewerID
        (UserID viewerID);

    /**
     * Sets to the given value a flag indicating whether an execution
     * report (ack, or broker response) for the receiver's order has
     * been persisted.
     *
     * @param persisted The flag.
     */

    public void setERPersisted
        (boolean erPersisted);

    /**
     * Sets to the given value a flag indicating whether the ORS is
     * expected to issue an ack for the receiver's order.
     *
     * @param ackExpected The flag.
     */

    public void setAckExpected
        (boolean ackExpected);

    /**
     * Sets to the given value a flag indicating whether the ORS has
     * retrieved the ack principals for the receiver's order.
     *
     * @param ackProcessed The flag.
     */
    
    public void setAckProcessed
        (boolean ackProcessed);

    /**
     * Sets to the given value a flag indicating whether the ORS is
     * expected to receive a response to the receiver's order.
     *
     * @param responseExpected The flag.
     */

    public void setResponseExpected
        (boolean responseExpected);
    
    /**
     * Sets to the given value a flag indicating whether the ORS has
     * retrieved the response principals for the receiver's order.
     *
     * @param responseProcessed The flag.
     */

    public void setResponseProcessed
        (boolean responseProcessed);

    /**
     * Notifies the receiver that the given message has been processed.
     *
     * @param msg The message.
     */
    
    public void setMessageProcessed
        (Message msg);
}
