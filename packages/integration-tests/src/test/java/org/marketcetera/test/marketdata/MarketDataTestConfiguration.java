package org.marketcetera.test.marketdata;

import java.util.List;

import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.test.MockSession;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import io.grpc.BindableService;
import quickfix.MessageFactory;

/* $License$ */

/**
 * Provides configuration for market data server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages={"org.marketcetera"})
public class MarketDataTestConfiguration
{
    /**
     * Get the message factory value.
     *
     * @return a <code>MessageFactory</code> value
     */
    @Bean
    public MessageFactory getMessageFactory()
    {
        return new quickfix.DefaultMessageFactory();
    }
    /**
     * Get the user service value.
     *
     * @return a <code>UserService</code> value
     */
    @Bean
    public UserService getUserService()
    {
        return new UserServiceImpl();
    }
    /**
     * Get the marketData client factory value.
     *
     * @return a <code>MarketDataRpcClientFactory</code> value
     */
    @Bean
    public MarketDataRpcClientFactory getMarketDataClientFactory()
    {
        return new MarketDataRpcClientFactory();
    }
    /**
     * Get the marketData RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager&lt;MockSession&gt;</code> value
     * @return a <code>MarketDataRpcService&lt;MockSession&gt;</code> value
     */
    @Bean
    public MarketDataRpcService<MockSession> getMarketDataRpcService(@Autowired Authenticator inAuthenticator,
                                                                     @Autowired SessionManager<MockSession> inSessionManager)
    {
        MarketDataRpcService<MockSession> marketDataRpcService = new MarketDataRpcService<>();
        marketDataRpcService.setAuthenticator(inAuthenticator);
        marketDataRpcService.setSessionManager(inSessionManager);
        return marketDataRpcService;
    }
    /**
     * Get the RPC server value.
     *
     * @param inServiceSpecs a <code>List&lt;BindableService&gt;</code> value
     * @return an <code>RpcServer</code> value
     * @throws Exception if the server cannot be created 
     */
    @Bean
    public RpcServer getRpcServer(@Autowired(required=false) List<BindableService> inServiceSpecs)
            throws Exception
    {
        RpcServer rpcServer = new RpcServer();
        rpcServer.setHostname(rpcHostname);
        rpcServer.setPort(rpcPort);
        if(inServiceSpecs != null) {
            for(BindableService service : inServiceSpecs) {
                rpcServer.getServerServiceDefinitions().add(service);
            }
        }
        return rpcServer;
    }
    /**
     * RPC hostname
     */
    @Value("${metc.rpc.hostname}")
    private String rpcHostname = "127.0.0.1";
    /**
     * RPC port
     */
    @Value("${metc.rpc.port}")
    private int rpcPort = 8999;
    /**
     * session life value in millis
     */
    @Value("${metc.session.life.mills}")
    private long sessionLife = SessionManager.INFINITE_SESSION_LIFESPAN;
}
