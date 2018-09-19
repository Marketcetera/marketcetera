package org.marketcetera.brokers;

import org.marketcetera.fix.ServerFixSession;

import quickfix.Message;

/* $License$ */

/**
 * Modify a FIX message before it is sent or after it is received from or to a session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MessageModifier
{
    /**
     * Modify the given message targeted to the given session.
     *
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     */
    boolean modify(ServerFixSession inServerFixSession,
                   Message inMessage);
}
