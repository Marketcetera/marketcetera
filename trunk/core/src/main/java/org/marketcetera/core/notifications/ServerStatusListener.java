package org.marketcetera.core.notifications;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A receiver of server connection status changes. Objects which need
 * to receive server connection status changes must implement this
 * interface, as well as register themselves with a compatible server.
 *
 * <p>It's expected that listeners will take a short time to return
 * because all listeners are invoked sequentially.  If a listener
 * takes too much time to process a status change, it will delay the
 * status delivery to other registered listeners.</p>
 *
 * @author tlerios@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface ServerStatusListener
{
    /**
     * Supplies the server connection status to the receiver.
     *
     * @param status The status: true means the server connection is
     * alive.
     */
    void receiveServerStatus(boolean status);
}
