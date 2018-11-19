package org.marketcetera.rpc.sample.client;

import org.marketcetera.rpc.client.RpcClientFactory;

/* $License$ */

/**
 * Creates {@link SampleRpcClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SampleRpcClientFactory
        implements RpcClientFactory<SampleRpcClientParameters,SampleRpcClient>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public SampleRpcClient create(SampleRpcClientParameters inParameters)
    {
        return new SampleRpcClient(inParameters);
    }
}
