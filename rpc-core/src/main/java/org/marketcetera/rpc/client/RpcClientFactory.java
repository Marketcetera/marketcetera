package org.marketcetera.rpc.client;

/* $License$ */

/**
 * Creates {@link RpcClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RpcClientFactory<RpcClientParametersClazz extends RpcClientParameters,RpcClientClazz extends RpcClient>
{
    /**
     * Create a factory suitable to construct the desired client factory.
     *
     * @param inParameters a <code>RpcClientParametersClazz</code> value
     * @return a <code>RpcClientClazz</code> value
     */
    RpcClientClazz create(RpcClientParametersClazz inParameters);
}
