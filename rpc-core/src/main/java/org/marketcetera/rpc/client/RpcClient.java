package org.marketcetera.rpc.client;

/* $License$ */

/**
 * Provides common behavior for an RPC client.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RpcClient<ParameterClazz extends RpcClientParameters>
{
    /**
     * 
     *
     *
     * @throws Exception
     */
    void start()
            throws Exception;
    /**
     * 
     *
     *
     * @throws Exception
     */
    void stop()
            throws Exception;
    /**
     * 
     *
     *
     * @return
     */
    boolean isRunning();
    /**
     * 
     *
     *
     * @return
     */
    ParameterClazz getParameters();
}
