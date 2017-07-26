package org.marketcetera.cluster;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 * Tests {@link SimpleClusterService}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/simple_cluster.xml" })
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
