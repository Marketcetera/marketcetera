package org.marketcetera.server.config;

import java.util.List;

import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.brokers.service.InMemoryFixSessionProvider;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.mdclient.MarketDataContextClassProvider;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.server.session.ServerSession;
import org.marketcetera.server.session.ServerSessionFactory;
import org.marketcetera.trade.impl.DefaultOwnerStrategy;
import org.marketcetera.trade.impl.OutgoingMessageLookupStrategy;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Lists;

import io.grpc.BindableService;
import quickfix.MessageFactory;

/* $License$ */

/**
 * Provides application configuration for Spring.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
@SpringBootConfiguration
public class ServerAppConfiguration
{
    /**
     * Get the module manager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    @Bean
    public ModuleManager getModuleManager()
    {
        ModuleManager moduleManager = new ModuleManager();
        moduleManager.init();
        return moduleManager;
    }
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
     * Get the session manager value.
     *
     * @param inServerSessionFactory a <code>ServerSessionFactory</code>value
     * @return a <code>SessionManager&lt;ServerSession&gt;</code> value
     */
    @Bean
    public SessionManager<ServerSession> getSessionManager(@Autowired ServerSessionFactory inServerSessionFactory)
    {
        SessionManager<ServerSession> sessionManager = new SessionManager<>(inServerSessionFactory,
                                                                            sessionLife);
        return sessionManager;
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
     * Get the market data RPC service.
     *
     * @param inAuthenticator
     * @param inSessionManager
     * @return a <code>MarketDataRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public MarketDataRpcService<ServerSession> getMarketDataRpcService(@Autowired Authenticator inAuthenticator,
                                                                       @Autowired SessionManager<ServerSession> inSessionManager)
    {
        MarketDataRpcService<ServerSession> marketDataRpcService = new MarketDataRpcService<>();
        // TODO need a market data service adapter
        marketDataRpcService.setContextClassProvider(MarketDataContextClassProvider.INSTANCE);
        marketDataRpcService.setAuthenticator(inAuthenticator);
        marketDataRpcService.setSessionManager(inSessionManager);
        return marketDataRpcService;
    }
    /**
     * Get the FIX session provider value.
     *
     * @return a <code>FixSessionProvider</code> value
     */
    @Bean
    public FixSessionProvider getFixSessionProvider()
    {
        return new InMemoryFixSessionProvider();
    }
    /**
     * Get the FIX session factory value.
     *
     * @return a <code>FixSessionFactory</code> value
     */
    @Bean
    public FixSessionFactory getFixSessionFactory()
    {
        return new SimpleFixSessionFactory();
    }
    /**
     * Get the message owner service value.
     *
     * @param inOutgoingMessageLookupStrategy an <code>OutgoingMessageLookupStrategy</code> value
     * @param inDefaultOwnerStrategy a <code>DefaultOwnerStrategy</code> value
     * @return a <code>MessageOwnerService</code> value
     */
    @Bean
    public MessageOwnerService getMessageOwnerService(@Autowired OutgoingMessageLookupStrategy inOutgoingMessageLookupStrategy,
                                                      @Autowired DefaultOwnerStrategy inDefaultOwnerStrategy)
    {
        MessageOwnerServiceImpl messageOwnerService = new MessageOwnerServiceImpl();
        messageOwnerService.setIdentifyOwnerStrategies(Lists.newArrayList(inOutgoingMessageLookupStrategy,inDefaultOwnerStrategy));
        return messageOwnerService;
    }
    /**
     * Get the outgoing message lookup strategy value.
     *
     * @return an <code>OutgoingMessageLookupStrategy</code> value
     */
    @Bean
    public OutgoingMessageLookupStrategy getOutgoingMessageLookupStrategy()
    {
        return new OutgoingMessageLookupStrategy();
    }
    /**
     * Get the default owner strategy value.
     *
     * @return a <code>DefaultOwnerStrategy</code> value
     */
    @Bean
    public DefaultOwnerStrategy getDefaultOwnerStrategy()
    {
        DefaultOwnerStrategy defaultOwnerStrategy = new DefaultOwnerStrategy();
        defaultOwnerStrategy.setUsername("trader");
        return defaultOwnerStrategy;
    }
    /**
     * message owner value
     */
    @Value("${metc.default.message.owner}")
    private String defaultMessageOwner = "trader";
    /**
     * session life value in millis
     */
    @Value("${metc.session.life.mills}")
    private long sessionLife = SessionManager.INFINITE_SESSION_LIFESPAN;
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
}
