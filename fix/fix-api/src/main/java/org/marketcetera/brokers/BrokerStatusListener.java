package org.marketcetera.brokers;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A receiver of broker status changes.
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
    void receiveBrokerStatus(BrokerStatus status);
}
