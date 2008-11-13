package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.io.Serializable;

/* $License$ */
/**
 * Common interface for Orders that can either be sent to any
 * broker / destination or are created to work with a
 * specific broker / destination.
 *
 * This message type is not meant to be used directly.
 *  
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Order extends Serializable {
    /**
     * Gets the security type for the Order.
     *
     * @return the security type for the Order.
     */
    SecurityType getSecurityType();

    /**
     * Gets the destinationID to which this order should be sent.
     * The destinationID can be optionally specified to over-ride the
     * default order routing mechanisms used on the server to route
     * this order to the appropriate broker / destination.
     *
     * @return the destinationID to send this order to.
     */
    DestinationID getDestinationID();

    /**
     * Sets the destinationID to which this order should be sent.
     *
     * @param inDestinationID the destinationID to send this order to.
     */
    void setDestinationID(DestinationID inDestinationID);
}
