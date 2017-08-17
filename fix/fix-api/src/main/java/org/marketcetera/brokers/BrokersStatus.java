package org.marketcetera.brokers;

import java.util.List;

/* $License$ */

/**
 * Contains the {@link BrokerStatus} for all brokers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokersStatus
{
    /**
     * Get the broker status values for all brokers.
     *
     * @return a <code>List&lt;BrokerStatus&gt;</code> value
     */
    List<BrokerStatus> getBrokers();
}
