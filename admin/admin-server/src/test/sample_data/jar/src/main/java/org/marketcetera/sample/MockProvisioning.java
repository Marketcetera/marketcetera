package org.marketcetera.sample;

import javax.annotation.PostConstruct;

import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Mock provisioning JAR provided to test {@link ProvisioningAgent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockProvisioning
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              getClass().getSimpleName());
        clusterService.setAttribute(getClass().getSimpleName(),
                                    String.valueOf(System.currentTimeMillis()));
    }
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
}
