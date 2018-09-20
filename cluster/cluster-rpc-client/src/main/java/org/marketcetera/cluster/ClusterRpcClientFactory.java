package org.marketcetera.cluster;

import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Create {@link ClusterClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClusterRpcClientFactory
        implements RpcClientFactory<ClusterRpcClientParameters,ClusterRpcClient>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public ClusterRpcClient create(ClusterRpcClientParameters inParameters)
    {
        ClusterRpcClient client = new ClusterRpcClient(inParameters);
        client.setClusterDataFactory(clusterDataFactory);
        client.setClusterMemberFactory(clusterMemberFactory);
        return client;
    }
    /**
     * creates {@link ClusterData} objects
     */
    @Autowired
    private ClusterDataFactory clusterDataFactory;
    /**
     * creates {@link ClusterMember} objects
     */
    @Autowired
    private ClusterMemberFactory clusterMemberFactory;
}
