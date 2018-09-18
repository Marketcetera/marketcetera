package org.marketcetera.cluster;

import org.marketcetera.rpc.client.RpcClientFactory;

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
        return new ClusterRpcClient(inParameters);
    }
}
