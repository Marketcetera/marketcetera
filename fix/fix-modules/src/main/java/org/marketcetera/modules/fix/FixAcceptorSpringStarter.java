package org.marketcetera.modules.fix;

import org.marketcetera.cluster.ClusterActivateWorkUnit;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterWorkUnit;
import org.marketcetera.cluster.ClusterWorkUnitType;
import org.marketcetera.cluster.ClusterWorkUnitUid;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides a Spring interface to the cluster service for the {@link FixAcceptorModule FIX acceptor module}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@ClusterWorkUnit(id="FIX.ACCEPTOR",type=ClusterWorkUnitType.SINGLETON_RUNTIME)
public class FixAcceptorSpringStarter
{
    /**
     * Active this module.
     *
     * @throws Exception if an error occurs activating the module
     */
    @ClusterActivateWorkUnit
    public void activate()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Activating {}",
                              getClusterWorkUnitUid());
        FixAcceptorModule.instance.activate();
    }
    /**
     * Get the cluster work unit id value.
     *
     * @return a <code>String</code> value
     */
    @ClusterWorkUnitUid
    public String getClusterWorkUnitUid()
    {
        if(clusterWorkUnitUid == null) {
            ClusterData clusterData = clusterService.getInstanceData();
            int instanceId = clusterData.getInstanceNumber();
            clusterWorkUnitUid = FixAcceptorModule.instance.getClass().getSimpleName() + "-" + instanceId;
        }
        return clusterWorkUnitUid;
    }
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * uniquely identifies this cluster work unit
     */
    private String clusterWorkUnitUid;
}
