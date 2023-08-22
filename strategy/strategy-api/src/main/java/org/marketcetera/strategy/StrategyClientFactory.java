//
// this file is automatically generated
//
package org.marketcetera.strategy;

/* $License$ */

/**
 * Creates {@link StrategyClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface StrategyClientFactory<ParameterClazz>
{
    /**
     * Create a new {@link StrategyClient} instance.
     *
     * @param inParameterClazz a <code>ParameterClazz</code> value
     * @return a <code>StrategyClient</code> value
     */
    StrategyClient create(ParameterClazz inParameterClazz);
}
