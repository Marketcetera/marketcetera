package org.marketcetera.strategyagent;

import org.marketcetera.core.ApplicationContainer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/* $License$ */

/**
 * Provides a test implementation of <code>ApplicationContainer</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockApplicationContainer
        extends ApplicationContainer
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationContainer#generateContext()
     */
    @Override
    protected ConfigurableApplicationContext generateContext()
    {
        ConfigurableApplicationContext context = new StaticApplicationContext();
        context.refresh();
        return context;
    }
}
