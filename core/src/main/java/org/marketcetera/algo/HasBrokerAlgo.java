package org.marketcetera.algo;

/* $License$ */

/**
 * Indicates that the underlying implementation has a {@link BrokerAlgo}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasBrokerAlgo
{
    /**
     * Gets the broker algo value, if any.
     *
     * @return a <code>BrokerAlgo</code> value
     */
    BrokerAlgo getBrokerAlgo();
}
