package org.marketcetera.server.config;

import java.util.List;

import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.brokers.service.InMemoryFixSessionProvider;
import org.marketcetera.core.ContextClassAggregator;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcService;
import org.marketcetera.marketdata.core.rpc.MarketDataServiceAdapter;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataContextClassProvider;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataServiceImpl;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.saclient.rpc.SAClientContextClassProvider;
import org.marketcetera.server.session.ServerSession;
import org.marketcetera.server.session.ServerSessionFactory;
import org.marketcetera.trade.TradeContextClassProvider;
import org.marketcetera.trade.impl.DefaultOwnerStrategy;
import org.marketcetera.trade.impl.OutgoingMessageLookupStrategy;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.marketcetera.util.rpc.RpcServer;
import org.marketcetera.util.rpc.RpcServiceSpec;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Lists;

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
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
     * @param inServiceSpecs a <code>List&lt;RpcServiceSpec&lt;ClientSessin&gt;&gt;</code> value
     * @return an <code>RpcServer&lt;ServerSession&gt;</code> value
     */
    @Bean
    public RpcServer<ServerSession> getRpcService(@Autowired Authenticator inAuthenticator,
                                                  @Autowired SessionManager<ServerSession> inSessionManager,
                                                  @Autowired List<RpcServiceSpec<ServerSession>> inServiceSpecs)
    {
        RpcServer<ServerSession> rpcServer = new RpcServer<>();
        ContextClassAggregator contextClassAggregator = new ContextClassAggregator();
        contextClassAggregator.setContextClassProviders(Lists.newArrayList(new TradeContextClassProvider(),new MarketDataContextClassProvider(),new SAClientContextClassProvider()));
        rpcServer.setContextClassProvider(contextClassAggregator);
        rpcServer.setHostname(rpcHostname);
        rpcServer.setPort(rpcPort);
        rpcServer.setAuthenticator(inAuthenticator);
        rpcServer.setSessionManager(inSessionManager);
        rpcServer.setServiceSpecs(inServiceSpecs);
        return rpcServer;
    }
    /**
     * Get the market data RPC service.
     *
     * @return a <code>MarketDataRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public MarketDataRpcService<ServerSession> getMarketDataRpcService(@Autowired SessionManager<ServerSession> inSessionManager)
    {
        MarketDataRpcService<ServerSession> marketDataRpcService = new MarketDataRpcService<>();
        MarketDataServiceAdapter serviceAdapter = new MarketDataServiceImpl<ServerSession>(inSessionManager);
        marketDataRpcService.setServiceAdapter(serviceAdapter);
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
