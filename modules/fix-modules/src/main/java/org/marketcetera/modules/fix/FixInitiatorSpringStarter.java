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
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@ClusterWorkUnit(id="FIX.INITIATOR",type=ClusterWorkUnitType.SINGLETON_RUNTIME)
public class FixInitiatorSpringStarter
{
    /**
     * 
     *
     *
     * @throws Exception
     */
    @ClusterActivateWorkUnit
    public void activate()
            throws Exception
    {
        SLF4JLoggerProxy.warn(this,
                              "COLIN: activating {}",
                              getClusterWorkUnitUid());
        FixInitiatorModule.instance.activate();
    }
    /**
     * 
     *
     *
     * @return
     */
    @ClusterWorkUnitUid
    public String getClusterWorkUnitUid()
    {
        if(clusterWorkUnitUid == null) {
            ClusterData clusterData = clusterService.getInstanceData();
            int instanceId = clusterData.getInstanceNumber();
            clusterWorkUnitUid = FixInitiatorModule.instance.getClass().getSimpleName() + "-" + instanceId;
        }
        return clusterWorkUnitUid;
    }
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * 
     */
    private String clusterWorkUnitUid;
}
