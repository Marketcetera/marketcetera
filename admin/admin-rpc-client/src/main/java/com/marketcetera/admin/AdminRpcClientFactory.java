package com.marketcetera.admin;

import org.marketcetera.admin.AdminClient;
import org.marketcetera.rpc.client.RpcClientFactory;

/* $License$ */

/**
 * Create {@link AdminClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcClientFactory
        implements RpcClientFactory<AdminRpcClientParameters,AdminRpcClient>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public AdminRpcClient create(AdminRpcClientParameters inParameters)
    {
        return new AdminRpcClient(inParameters);
    }
}
