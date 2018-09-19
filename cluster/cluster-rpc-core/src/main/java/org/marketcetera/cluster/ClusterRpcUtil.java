package org.marketcetera.cluster;

import java.util.Optional;

import org.marketcetera.cluster.rpc.ClusterRpc;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClusterRpcUtil
{
    /**
     *
     *
     * @param inInstanceData
     * @return
     */
    public static Optional<InstanceData> getInstanceData(ClusterRpc.InstanceData inInstanceData)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
    *
    *
    * @param inRpcClusterData
    * @return
    */
   public static Optional<ClusterData> getClusterData(ClusterRpc.ClusterData inRpcClusterData)
   {
       throw new UnsupportedOperationException(); // TODO
   }
    /**
     *
     *
     * @return
     */
    public static Optional<ClusterRpc.ClusterData> getRpcClusterData(ClusterData inClusterData)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
}
