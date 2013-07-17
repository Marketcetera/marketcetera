package org.marketcetera.client.jms;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A type-safe message handler.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface ReceiveOnlyHandler<T>
{
    /**
     * Handles the given message. Changing the name of this method
     * requires changing the implementation of {@link
     * IncomingJmsFactory} as well.
     *
     * @param msg The message.
     */

    void receiveMessage
        (T msg);
}
