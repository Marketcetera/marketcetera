package org.marketcetera.fix;

import org.marketcetera.trade.HasBrokerID;

/* $License$ */

/**
 * Indicates that the implementor has a {@link FixSessionStatus} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasFixSessionStatus
        extends HasSessionId,HasBrokerID
{
    /**
     * Get the FIX session status value.
     *
     * @return a <code>FixSessionStatus</code> value
     */
    FixSessionStatus getFixSessionStatus();
}
