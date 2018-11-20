package org.marketcetera.trade;

import org.marketcetera.admin.User;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Represents a message sent to a broker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OutgoingMessage
{
    /**
     * Gets the broker ID value.
     *
     * @return a <code>BrokerID</code> value
     */
    BrokerID getBrokerId();
    /**
     * Gets the sender comp ID value.
     *
     * @return a <code>String</code> value
     */
    String getSenderCompId();
    /**
     * Gets the target comp ID value.
     *
     * @return a <code>String</code> value
     */
    String getTargetCompId();
    /**
     * Gets the internal object id.
     *
     * @return a <code>long</code> value
     */
    long getId();
    /**
     * Gets the FIX message.
     *
     * @return a <code>Message</code> value
     */
    Message getMessage();
    /**
     * Gets the session id value.
     *
     * @return a <code>SessionID</code> value
     */
    SessionID getSessionID();
    /**
     * Gets the FIX message type of the underlying message.
     *
     * @return a <code>String</code> value
     */
    String getMessageType();
    /**
     * Gets the message sequence number value.
     *
     * @return an <code>int</code> value
     */
    int getMsgSeqNum();
    /**
     * Get the owner of the outgoing order.
     *
     * @return a <code>User</code> value
     */
    User getActor();
    /**
     * Get the order id reference of the outgoing message.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    String getOrderId();
}
