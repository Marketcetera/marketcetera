package org.marketcetera.fix;

import java.util.Date;

import quickfix.Message;

/* $License$ */

/**
 * Represents an incoming FIX message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IncomingMessage
{
    /**
     * Gets the uniquely identifying ID value.
     *
     * @return a <code>long</code> value
     */
    long getId();
    /**
     * Gets the session ID value.
     *
     * @return a <code>String</code> value
     */
    String getSessionId();
    /**
     * Gets the message sequence number.
     *
     * @return an <code>int</code> value
     */
    int getMsgSeqNum();
    /**
     * Gets the sending time value.
     *
     * @return a <code>Date</code> value
     */
    Date getSendingTime();
    /**
     * Gets the msg type value.
     *
     * @return a <code>String</code> value
     */
    String getMsgType();
    /**
     * Get the exec id, if present.
     *
     * @return a <code>String</code> value
     */
    String getExecId();
    /**
     * Get the cl ord id, if present.
     *
     * @return a <code>String</code> value
     */
    String getClOrdId();
    /**
     * Gets the FIX message value.
     *
     * @return a <code>Message</code> value
     */
    Message getMessage();
}
