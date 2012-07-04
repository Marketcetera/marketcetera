package org.marketcetera.core.trade;

import java.io.Serializable;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Common interface for Orders that can either be sent to any
 * broker or are created to work with a
 * specific broker.
 *
 * This message type is not meant to be used directly.
 *  
 * @author anshul@marketcetera.com
 * @version $Id: Order.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: Order.java 16063 2012-01-31 18:21:55Z colin $")
public interface Order extends Serializable {
    /**
     * Gets the security type for the Order.
     *
     * @return the security type for the Order.
     */
    SecurityType getSecurityType();

    /**
     * Gets the brokerID to which this order should be sent.
     * The brokerID can be optionally specified to over-ride the
     * default order routing mechanisms used on the server to route
     * this order to the appropriate broker.
     *
     * @return the brokerID to send this order to.
     */
    BrokerID getBrokerID();

    /**
     * Sets the brokerID to which this order should be sent.
     *
     * @param inBrokerID the brokerID to send this order to.
     */
    void setBrokerID(BrokerID inBrokerID);
}
