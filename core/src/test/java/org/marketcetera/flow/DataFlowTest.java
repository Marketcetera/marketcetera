package org.marketcetera.flow;

import org.junit.Test;
import org.marketcetera.core.IntegrationTestBase;
import org.marketcetera.service.ServiceManager;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowTest
        extends IntegrationTestBase
{
    @Test
    public void testOne()
            throws Exception
    {
        ServiceManager dataFlowManager = getApplicationContext().getBean(ServiceManager.class);
    }
}
