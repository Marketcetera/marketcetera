package org.marketcetera.cluster;

import java.util.Optional;

import org.marketcetera.cluster.rpc.ClusterRpc;
import org.marketcetera.cluster.service.ClusterMember;

/* $License$ */

/**
 * Provides cluster RPC utilities.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClusterRpcUtil
{
    /**
     * Get the RPC value from the given value.
     *
     * @param inClusterData a <code>ClusterData</code> value
     * @return an <code>Optional&lt;ClusterRpc.ClusterData&gt;</code> value
     */
    public static Optional<ClusterRpc.ClusterData> getRpcClusterData(ClusterData inClusterData)
    {
        if(inClusterData == null) {
            return Optional.empty();
        }
        ClusterRpc.ClusterData.Builder builder = ClusterRpc.ClusterData.newBuilder();
        builder.setHostId(inClusterData.getHostId());
        builder.setInstanceNumber(inClusterData.getInstanceNumber());
        builder.setTotalInstances(inClusterData.getTotalInstances());
        builder.setUuid(inClusterData.getUuid());
        return Optional.of(builder.build());
    }
    /**
     * Get the cluster data value from the given RPC value.
     *
     * @param inRpcClusterData a <code>ClusterRpc.ClusterData</code> value
     * @param inClusterDataFactory a <code>ClusterDataFactory</code> value
     * @return an <code>Optional&lt;ClusterData&gt;</code> value
     */
    public static Optional<ClusterData> getClusterData(ClusterRpc.ClusterData inRpcClusterData,
                                                       ClusterDataFactory inClusterDataFactory)
    {
        ClusterData clusterData = inClusterDataFactory.create(inRpcClusterData.getTotalInstances(),
                                                              inRpcClusterData.getHostId(),
                                                              inRpcClusterData.getHostNumber(),
                                                              inRpcClusterData.getInstanceNumber(),
                                                              inRpcClusterData.getUuid());
        return(clusterData == null ? Optional.empty() : Optional.of(clusterData));
    }
    /**
     *
     *
     * @param inClusterMember
     * @return
     */
    public static Optional<ClusterRpc.ClusterMember> getRpcClusterMember(ClusterMember inClusterMember)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     *
     *
     * @param inRpcClusterMember
     * @param inClusterMemberFactory
     * @return
     */
    public static Optional<ClusterMember> getClusterMember(ClusterRpc.ClusterMember inRpcClusterMember,
                                                           ClusterMemberFactory inClusterMemberFactory)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
}
