package org.marketcetera.brokers;

/* $License$ */

/**
 * Broadcasts {@link BrokerStatus} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokerStatusBroadcaster
{
    /**
     * Receives a broker status to broadcast to interested subscribers.
     *
     * @param inBrokerStatus a <code>BrokerStatus</code> value
     */
    void reportBrokerStatus(BrokerStatus inBrokerStatus);
}
