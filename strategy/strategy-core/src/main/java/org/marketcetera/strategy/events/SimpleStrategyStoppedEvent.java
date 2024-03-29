//
// this file is automatically generated
//
package org.marketcetera.strategy.events;

import org.marketcetera.core.Preserve;
import org.marketcetera.strategy.StrategyInstance;

/* $License$ */

/**
 * Indicates that a strategy was stopped.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public class SimpleStrategyStoppedEvent
        implements StrategyStoppedEvent
{
    /**
     * Create a new SimpleStrategyStoppedEvent instance.
     */
    public SimpleStrategyStoppedEvent() {}
    /**
     * Create a new SimpleStrategyStoppedEvent instance.
     *
     * @param inStrategyInstance a <code>StrategyInstance</code> value
     */
    public SimpleStrategyStoppedEvent(StrategyInstance inStrategyInstance)
    {
        strategyInstance = inStrategyInstance;
    }
    /**
     * Get the strategyInstance value.
     *
     * @return an <code>org.marketcetera.strategy.StrategyInstance</code> value
     */
    @Override
    public org.marketcetera.strategy.StrategyInstance getStrategyInstance()
    {
        return strategyInstance;
    }
    /**
     * Set the strategyInstance value.
     *
     * @param inStrategyInstance an <code>org.marketcetera.strategy.StrategyInstance</code> value
     */
    public void setStrategyInstance(org.marketcetera.strategy.StrategyInstance inStrategyInstance)
    {
        strategyInstance = inStrategyInstance;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("StrategyStoppedEvent [")
            .append("strategyInstance=").append(strategyInstance).append("]");
        return builder.toString();
    }
    /**
     * strategy instance which was successfully stopped
     */
    private org.marketcetera.strategy.StrategyInstance strategyInstance;
}
