//
// this file is automatically generated
//
package org.marketcetera.strategy.events;

/* $License$ */

/**
 * Indicates that a strategy did not start successfully.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface StrategyStartFailedEvent
        extends org.marketcetera.strategy.events.StrategyEvent,org.marketcetera.strategy.HasStrategyInstance
{
    /**
     * Get the errorMessage value.
     *
     * @return a <code>String</code> value
     */
    String getErrorMessage();
    /**
     * Set the errorMessage value.
     *
     * @param inErrorMessage a <code>String</code> value
     */
    void setErrorMessage(String inErrorMessage);
}
