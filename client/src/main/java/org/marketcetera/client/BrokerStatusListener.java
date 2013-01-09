package org.marketcetera.client;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A receiver of broker status changes. Objects which need to
 * receive broker status changes must implement this interface,
 * as well as register themselves with a client via {@link
 * Client#addBrokerStatusListener(BrokerStatusListener)}.
 *
 * <p>It's expected that listeners will take a short time to return
 * because all listeners are invoked sequentially.  If a listener
 * takes too much time to process a status change, it will delay the
 * status delivery to other registered listeners.</p>
 *
 * @author tlerios@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */

/* $License$ */

@ClassVersion("$Id$")
public interface BrokerStatusListener
{
    /**
     * Supplies a broker status to the receiver.
     *
     * @param status The status.
     */

    void receiveBrokerStatus
        (BrokerStatus status);
}
