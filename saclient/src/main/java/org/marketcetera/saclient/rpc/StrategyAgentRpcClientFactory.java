package org.marketcetera.saclient.rpc;

import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.saclient.SAClientFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates {@link RpcSAClient} instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class StrategyAgentRpcClientFactory
        implements RpcClientFactory<StrategyAgentRpcClientParameters,StrategyAgentRpcClient>,SAClientFactory<StrategyAgentRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public StrategyAgentRpcClient create(StrategyAgentRpcClientParameters inParameters)
    {
        return new StrategyAgentRpcClient(inParameters);
    }
    /**
     * factory instance value
     */
    public static final StrategyAgentRpcClientFactory INSTANCE = new StrategyAgentRpcClientFactory();
}
