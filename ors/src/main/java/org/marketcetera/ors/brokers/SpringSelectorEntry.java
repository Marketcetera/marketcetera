package org.marketcetera.ors.brokers;

import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The Spring-based configuration of a selector entry.
 *
 * @author tlerios@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface SpringSelectorEntry
{
    /**
     * Indicates if the given order should be routed to this broker.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>boolean</code> value
     */
    public boolean routeToBroker(Order inOrder);
    /**
     * Sets the broker value.
     *
     * @param inBroker a <code>SpringBroker</code> value
     */
    public void setBroker(SpringBroker inBroker);
    /**
     * Gets the broker value.
     *
     * @return a <code>SpringBroker</code> value
     */
    public SpringBroker getBroker();
    /**
     * Indicates if this selector should be skipped if the broker is unavailable.
     *
     * @param inSkipIfUnavailable a <code>boolean</code> value
     */
    public void setSkipIfUnavailable(boolean inSkipIfUnavailable);
    /**
     * Indicates if this selector should be skipped if the broker is unavailable.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getSkipIfUnavailable();
}
