package org.marketcetera.client;

import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A receiver of destination status changes. Objects which need to
 * receive destination status changes must implement this interface,
 * as well as register themselves with a client via {@link
 * Client#addDestinationStatusListener(DestinationStatusListener)}.
 *
 * <p>It's expected that listeners will take a short time to return
 * because all listeners are invoked sequentially.  If a listener
 * takes too much time to process a status change, it will delay the
 * status delivery to other registered listeners.</p>
 *
 * @author tlerios@marketcetera.com
 * @version $Id$
 * @since $Release$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public interface DestinationStatusListener
{
    /**
     * Supplies a destination status to the receiver.
     *
     * @param status The status.
     */

    void receiveDestinationStatus
        (DestinationStatus status);
}
