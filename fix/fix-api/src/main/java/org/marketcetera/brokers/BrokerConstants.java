package org.marketcetera.brokers;

/* $License$ */

/**
 * Defines constants for brokers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BrokerConstants
{
    /**
     * cluster attribute key used to indicate broker status
     */
    static final String brokerStatusPrefix = "metc.broker.status-";
    /**
     * key used to indicate session customization for a session
     */
    static final String sessionCustomizationKey = "org.marketcetera.sessioncustomization";
    /**
     * key used to indicate active days for a session
     */
    static final String sessionDaysKey = "org.marketcetera.sessiondays";
    /**
     * logging category to use for broker status
     */
    static final String brokerStatusCategory = "metc.brokers";
}
