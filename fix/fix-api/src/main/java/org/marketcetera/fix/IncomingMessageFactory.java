package org.marketcetera.fix;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Creates {@link IncomingMessage} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IncomingMessageFactory
{
    /**
     * Creates a new {@link IncomingMessage} value.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     * @return an <code>IncomingMessage</code> value
     */
    IncomingMessage create(SessionID inSessionId,
                           Message inMessage);
}
