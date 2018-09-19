package org.marketcetera.fix;

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
        return fixAdminRpcClient;
    }
    /**
     * creates {@link ActiveFixSession} objects
     */
    @Autowired
    private MutableActiveFixSessionFactory activeFixSessionFactory;
}
