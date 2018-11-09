package org.marketcetera.fix;

import quickfix.Message;

/* $License$ */

/**
 * Represents an actual FIX message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FixMessage.java 17046 2016-11-02 18:03:23Z colin $
 * @since 2.5.0
 */
public interface FixMessage
{
    /**
     * Gets the message contents.
     *
     * @return a <code>String</code> value
     */
    String getMessage();
    /**
     * Sets the message contents.
     *
     * @param inMessage a <code>String</code> value
     */
    void setMessage(String inMessage);
    /**
     * Sets the message value.
     *
     * @param inMessage a <code>Message</code> value
     */
    void setMessage(Message inMessage);
}
