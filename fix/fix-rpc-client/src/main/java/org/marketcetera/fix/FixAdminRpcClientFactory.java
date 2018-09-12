package org.marketcetera.fix;

import org.marketcetera.rpc.client.RpcClientFactory;

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
        return new FixAdminRpcClient(inParameters);
    }
}
