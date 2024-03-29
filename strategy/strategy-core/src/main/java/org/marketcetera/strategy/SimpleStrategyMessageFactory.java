//
// this file is automatically generated
//
package org.marketcetera.strategy;

import org.marketcetera.core.Preserve;

/* $License$ */

/**
 * Creates new {@link SimpleStrategyMessage} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public class SimpleStrategyMessageFactory
        implements org.marketcetera.strategy.StrategyMessageFactory
{
    /**
     * Create a new <code>org.marketcetera.strategy.SimpleStrategyMessage</code> instance.
     *
     * @return a <code>org.marketcetera.strategy.SimpleStrategyMessage</code> value
     */
    @Override
    public org.marketcetera.strategy.SimpleStrategyMessage create()
    {
        return new org.marketcetera.strategy.SimpleStrategyMessage();
    }
    /**
     * Create a new <code>org.marketcetera.strategy.SimpleStrategyMessage</code> instance from the given object.
     *
     * @param inStrategyMessage an <code>org.marketcetera.strategy.SimpleStrategyMessage</code> value
     * @return an <code>org.marketcetera.strategy.SimpleStrategyMessage</code> value
     */
    @Override
    public org.marketcetera.strategy.SimpleStrategyMessage create(org.marketcetera.strategy.StrategyMessage inStrategyMessage)
    {
        return new org.marketcetera.strategy.SimpleStrategyMessage(inStrategyMessage);
    }
}
