package org.marketcetera.cluster;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/* $License$ */

/**
 * Tests {@link SimpleClusterService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=SimpleClusterTestConfiguration.class)
public class SimpleClusterServiceTest
        extends ClusterTestBase<SimpleClusterService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.cluster.ClusterTestBase#createClusterService()
     */
    @Override
    protected SimpleClusterService createClusterService()
    {
        return applicationContext.getBean(SimpleClusterService.class);
    }
    /**
     * provides access to the test application context
     */
    @Autowired
    private ApplicationContext applicationContext;
}
