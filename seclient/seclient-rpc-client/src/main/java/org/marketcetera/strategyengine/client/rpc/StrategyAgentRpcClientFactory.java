package org.marketcetera.strategyengine.client.rpc;

import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.strategyengine.client.SEClientFactory;
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
        implements RpcClientFactory<SERpcClientParameters,SERpcClient>,SEClientFactory<SERpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public SERpcClient create(SERpcClientParameters inParameters)
    {
        return new SERpcClient(inParameters);
    }
    /**
     * factory instance value
     */
    public static final StrategyAgentRpcClientFactory INSTANCE = new StrategyAgentRpcClientFactory();
}
