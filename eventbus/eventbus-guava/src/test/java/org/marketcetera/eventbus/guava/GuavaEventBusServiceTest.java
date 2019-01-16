package org.marketcetera.eventbus.guava;

import org.marketcetera.eventbus.EventBusTestBase;

/* $License$ */

/**
 * Tests {@link GuavaEventBusService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class GuavaEventBusServiceTest
        extends EventBusTestBase<GuavaEventBusService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EventBusTestBase#createService()
     */
    @Override
    protected GuavaEventBusService createService()
    {
        GuavaEventBusService service = new GuavaEventBusService();
        service.start();
        return service;
    }
}
