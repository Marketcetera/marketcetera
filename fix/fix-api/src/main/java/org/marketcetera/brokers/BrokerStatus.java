package org.marketcetera.brokers;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Contains the status and settings of a broker connection.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokerStatus
        extends Serializable
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    String getName();
    /**
     * Get the brokerId value.
     *
     * @return a <code>BrokerID</code> value
     */
    BrokerID getId();
    /**
     * Get the loggedOn value.
     *
     * @return a <code>boolean</code> value
     */
    boolean getLoggedOn();
    /**
     * Indicate if the broker is an initiator.
     *
     * @return a <code>boolean</code> value
     */
    boolean isInitiator();
    /**
     * Get the brokerAlgos value.
     *
     * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    Set<BrokerAlgoSpec> getBrokerAlgos();
    /**
     * Get the settings value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    Map<String,String> getSettings();
    /**
     * Gets the host name value.
     *
     * @return a <code>String</code> value
     */
    String getHost();
    /**
     * Gets the host port value.
     *
     * @return an <code>int</code> value
     */
    int getPort();
    /**
     * Get the session status value.
     *
     * @return a <code>FixSessionStatus</code> value
     */
    FixSessionStatus getStatus();
}
