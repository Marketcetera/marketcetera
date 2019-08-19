package org.marketcetera.dataflow.client.rpc;

import org.marketcetera.dataflow.client.DataFlowClientFactory;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates {@link DataFlowRpcClient} instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class DataFlowRpcClientFactory
        implements RpcClientFactory<DataFlowRpcClientParameters,DataFlowRpcClient>,DataFlowClientFactory<DataFlowRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public DataFlowRpcClient create(DataFlowRpcClientParameters inParameters)
    {
        return new DataFlowRpcClient(inParameters);
    }
    /**
     * factory instance value
     */
    public static final DataFlowRpcClientFactory INSTANCE = new DataFlowRpcClientFactory();
}
