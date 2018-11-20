package org.marketcetera.marketdata.rpc;

import org.marketcetera.marketdata.core.rpc.MarketDataRpcServiceGrpc;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClient;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.rpc.RpcTestBase;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Tests the market data RPC client and server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRpcClientServerTest.java 17251 2016-09-08 23:18:29Z colin $
 * @since 2.4.0
 */
public class MarketDataRpcServiceTest
        extends RpcTestBase<MarketDataRpcClientParameters,MarketDataRpcClient,SessionId,MarketDataRpcServiceGrpc.MarketDataRpcServiceImplBase,MarketDataRpcService<SessionId>>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getRpcClientFactory()
     */
    @Override
    protected RpcClientFactory<MarketDataRpcClientParameters, MarketDataRpcClient> getRpcClientFactory()
    {
        return new MarketDataRpcClientFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.RpcTestBase#getClientParameters(java.lang.String, int, java.lang.String, java.lang.String)
     */
    @Override
    protected MarketDataRpcClientParameters getClientParameters(String inHostname,
                                                                int inPort,
                                                                String inUsername,
                                                                String inPassword)
    {
        MarketDataRpcClientParameters parameters = new MarketDataRpcClientParameters();
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
    protected MarketDataRpcService<SessionId> createTestService()
    {
        MockMarketDataServiceAdapter serviceAdapter = new MockMarketDataServiceAdapter();
        MarketDataRpcService<SessionId> service = new MarketDataRpcService<>();
        service.setServiceAdapter(serviceAdapter);
        return service;
    }
}
