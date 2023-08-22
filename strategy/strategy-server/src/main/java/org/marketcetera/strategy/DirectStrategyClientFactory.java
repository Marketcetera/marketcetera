package org.marketcetera.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 * Strategy Client implementation to use when strategy services are embedded directly in the main server container.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DirectStrategyClientFactory
        implements StrategyClientFactory<DirectStrategyClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyClientFactory#create(java.lang.Object)
     */
    @Override
    public DirectStrategyClient create(DirectStrategyClientParameters inParameterClazz)
    {
        DirectStrategyClient strategyClient = new DirectStrategyClient(applicationContext,
                                                                       inParameterClazz.getUsername());
        return strategyClient;
    }
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
}
