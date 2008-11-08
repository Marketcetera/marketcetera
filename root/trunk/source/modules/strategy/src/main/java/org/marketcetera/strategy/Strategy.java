package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Represents the actual strategy to execute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
interface Strategy
    extends Lifecycle
{
    /**
     * Sends data received from an external source to a strategy.
     *
     * @param inData an <code>Object</code> value
     */
    public void dataReceived(Object inData);
}
