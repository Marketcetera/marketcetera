package org.marketcetera.rpc.client;

import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.sample.SampleRpcService;
import org.marketcetera.rpc.sample.SampleRpcServiceGrpc;
import org.marketcetera.rpc.sample.client.SampleRpcClient;
import org.marketcetera.rpc.sample.client.SampleRpcClientFactory;
import org.marketcetera.rpc.sample.client.SampleRpcClientParameters;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests {@link AbstractRpcClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RpcClientTest
        extends RpcTestBase<SampleRpcClientParameters,SampleRpcClient,SessionId,SampleRpcServiceGrpc.SampleRpcServiceImplBase,SampleRpcService<SessionId>>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<SampleRpcClientParameters,SampleRpcClient> getRpcClientFactory()
    {
        return new SampleRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected SampleRpcClientParameters getClientParameters(String inHostname,
                                                            int inPort,
                                                            String inUsername,
                                                            String inPassword)
    {
        SampleRpcClientParameters parameters = new SampleRpcClientParameters();
        parameters.setHeartbeatInterval(1000);
        parameters.setHostname(inHostname);
        parameters.setPassword(inPassword);
        parameters.setPort(inPort);
        parameters.setUsername(inUsername);
        return parameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#createTestService()
     */
    @Override
    protected SampleRpcService<SessionId> createTestService()
    {
        return new SampleRpcService<SessionId>();
    }
}
