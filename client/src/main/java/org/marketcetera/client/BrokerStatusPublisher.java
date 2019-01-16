package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides broker status to listeners.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface BrokerStatusPublisher
{
    /**
     * Adds a broker status listener, which receives all the broker status changes sent out by the server.
     *
     * <p>If the same listener is added more than once, it will receive notifications as many times as it has been added.</p>
     *
     * <p>The listeners are notified in the reverse order of their addition.</p>
     *
     * @param inListener a <code>BrokerStatusListener</code> value
     */
    public void addBrokerStatusListener(BrokerStatusListener inListener);
    /**
     * Removes a broker status listener that was previously added via {@link #addBrokerStatusListener(BrokerStatusListener)}.
     *
     * <p>If the listener was added more than once, only its most
     * recently added instance will be removed.</p>
     *
     * @param inListener a <code>BrokerStatusListener</code> value
     */
    public void removeBrokerStatusListener(BrokerStatusListener inListener);
}
