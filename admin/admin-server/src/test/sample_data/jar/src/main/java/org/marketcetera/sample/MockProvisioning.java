package org.marketcetera.sample;

import jakarta.annotation.PostConstruct;

import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Mock provisioning JAR provided to test {@link ProvisioningAgent}.
 * Enhanced version with better Jakarta EE compatibility and robust error handling.
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
        try {
            SLF4JLoggerProxy.info(this,
                                  "Starting {}, Jakarta EE compatible version",
                                  getClass().getSimpleName());
            
            if (clusterService == null) {
                SLF4JLoggerProxy.error(this, 
                        "ClusterService autowiring failed - service is null");
                return;
            }
            
            // Set an initial start attribute for troubleshooting
            clusterService.setAttribute("StartProvisioning", 
                    "Started at " + System.currentTimeMillis());
            
            // Set the main attribute the test looks for
            clusterService.setAttribute(getClass().getSimpleName(),
                                       String.valueOf(System.currentTimeMillis()));
            
            // Log success
            SLF4JLoggerProxy.info(this,
                                 "{} successfully set attribute in cluster service",
                                 getClass().getSimpleName());
            
        } catch (Exception e) {
            // Comprehensive error logging to help troubleshoot
            SLF4JLoggerProxy.error(this, e, 
                    "Error during {} provisioning: {}",
                    getClass().getSimpleName(), 
                    e.getMessage());
        }
    }
    
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
}
