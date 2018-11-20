package org.marketcetera.fix;

import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Create {@link FixAdminClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAdminRpcClientFactory
        implements RpcClientFactory<FixAdminRpcClientParameters,FixAdminRpcClient>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public FixAdminRpcClient create(FixAdminRpcClientParameters inParameters)
    {
        FixAdminRpcClient fixAdminRpcClient = new FixAdminRpcClient(inParameters);
        fixAdminRpcClient.setActiveFixSessionFactory(activeFixSessionFactory);
        fixAdminRpcClient.setFixSessionFactory(fixSessionFactory);
        fixAdminRpcClient.setFixSessionAttributeDescriptorFactory(fixSessionAttributeDescriptorFactory);
        fixAdminRpcClient.setClusterDataFactory(clusterDataFactory);
        return fixAdminRpcClient;
    }
    /**
     * creates {@link FixSessionAttributeDescriptor} objects
     */
    @Autowired
    private FixSessionAttributeDescriptorFactory fixSessionAttributeDescriptorFactory;
    /**
     * creates {@link ActiveFixSession} objects
     */
    @Autowired
    private MutableActiveFixSessionFactory activeFixSessionFactory;
    /**
     * creates {@link FixSession} objects
     */
    @Autowired
    private MutableFixSessionFactory fixSessionFactory;
    /**
     * creates {@link ClusterData} objects
     */
    @Autowired
    private ClusterDataFactory clusterDataFactory;
}
