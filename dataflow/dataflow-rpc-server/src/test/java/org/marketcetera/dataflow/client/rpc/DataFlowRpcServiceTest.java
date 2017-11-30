package org.marketcetera.dataflow.client.rpc;

import org.marketcetera.dataflow.rpc.DataFlowContextClassProvider;
import org.marketcetera.dataflow.rpc.DataFlowRpcServiceGrpc;
import org.marketcetera.dataflow.server.rpc.DataFlowRpcService;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests {@link DataFlowRpcClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RpcSAClientImplTest.java 17223 2016-08-31 01:03:01Z colin $
 * @since 2.4.0
 */
public class DataFlowRpcServiceTest
        extends RpcTestBase<DataFlowRpcClientParameters,DataFlowRpcClient,SessionId,DataFlowRpcServiceGrpc.DataFlowRpcServiceImplBase,DataFlowRpcService<SessionId>>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected DataFlowRpcService<SessionId> createTestService()
    {
        DataFlowRpcService<SessionId> service = new DataFlowRpcService<>();
        return service;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<DataFlowRpcClientParameters,DataFlowRpcClient> getRpcClientFactory()
    {
        return new DataFlowRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected DataFlowRpcClientParameters getClientParameters(String inHostname,
                                                                   int inPort,
                                                                   String inUsername,
                                                                   String inPassword)
    {
        DataFlowRpcClientParameters parameters = new DataFlowRpcClientParameters();
        parameters.setContextClassProvider(DataFlowContextClassProvider.INSTANCE);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
}
