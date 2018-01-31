package org.marketcetera.server;

import java.util.Collection;
import java.util.List;

import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.dao.PersistentUserAttributeFactory;
import org.marketcetera.admin.rpc.AdminRpcService;
import org.marketcetera.admin.service.UserAttributeService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserAttributeServiceImpl;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.brokers.Selector;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.brokers.service.InMemoryFixSessionProvider;
import org.marketcetera.client.rpc.server.TradeClientRpcService;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.dataflow.config.DataFlowProvider;
import org.marketcetera.dataflow.server.rpc.DataFlowRpcService;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.server.session.ServerSession;
import org.marketcetera.server.session.ServerSessionFactory;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.BasicSelector;
import org.marketcetera.trade.config.StandardIncomingDataFlowProvider;
import org.marketcetera.trade.config.StandardOutgoingDataFlowProvider;
import org.marketcetera.trade.config.StandardReportInjectionDataFlowProvider;
import org.marketcetera.trade.impl.DefaultOwnerStrategy;
import org.marketcetera.trade.impl.OutgoingMessageLookupStrategy;
import org.marketcetera.trade.modules.OrderConverterModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessageCachingModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessagePersistenceModuleFactory;
import org.marketcetera.trade.modules.TradeMessageBroadcastModuleFactory;
import org.marketcetera.trade.modules.TradeMessageConverterModuleFactory;
import org.marketcetera.trade.modules.TradeMessagePersistenceModuleFactory;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.PortUserProxy;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.collect.Lists;

import io.grpc.BindableService;
import quickfix.MessageFactory;

/* $License$ */

/**
 * Application entry point for the web UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@EnableTransactionManagement
@EntityScan(basePackages={"org.marketcetera"})
@SpringBootApplication(scanBasePackages={"org.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera"})
public class ServerApplication
{
    /**
     * Main application entry.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(ServerApplication.class,
                              inArgs);
        ApplicationContainer.main(inArgs);
    }
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
        ModuleManager.startModulesIfNecessary(moduleManager,
                                              BogusFeedModuleFactory.INSTANCE_URN,
                                              TransactionModuleFactory.INSTANCE_URN,
                                              TradeMessageConverterModuleFactory.INSTANCE_URN,
                                              TradeMessagePersistenceModuleFactory.INSTANCE_URN,
                                              TradeMessageBroadcastModuleFactory.INSTANCE_URN,
                                              OrderConverterModuleFactory.INSTANCE_URN,
                                              OutgoingMessageCachingModuleFactory.INSTANCE_URN,
                                              OutgoingMessagePersistenceModuleFactory.INSTANCE_URN,
                                              FixInitiatorModuleFactory.INSTANCE_URN);
        for(DataFlowProvider dataFlowProvider : dataFlowProviders) {
            SLF4JLoggerProxy.info(this,
                                  "Starting {}",
                                  dataFlowProvider);
            try {
                dataFlowProvider.receiveDataFlowId(moduleManager.createDataFlow(dataFlowProvider.getDataFlow(moduleManager)));
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Unable to start data flow: " + dataFlowProvider.getName(),
                                                 e);
            }
        }
        return moduleManager;
    }
    /**
     * Create the standard incoming data flow.
     *
     * @return a <code>DataFlowProvider</code> value
     */
    @Bean
    public DataFlowProvider getIncomingDataFlow()
    {
        return new StandardIncomingDataFlowProvider();
    }
    /**
     * Create the standard outgoing data flow.
     *
     * @return a <code>DataFlowProvider</code> value
     */
    @Bean
    public DataFlowProvider getOutgoingDataFlow()
    {
        return new StandardOutgoingDataFlowProvider();
    }
    /**
     * Create the standard report injection data flow.
     *
     * @return a <code>DataFlowProvider</code> value
     */
    @Bean
    public DataFlowProvider getInjectionDataFlow()
    {
        return new StandardReportInjectionDataFlowProvider();
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
     * Get the trade RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>TradeClientRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public TradeClientRpcService<ServerSession> getTradeRpcTradeService(@Autowired Authenticator inAuthenticator,
                                                                        @Autowired SessionManager<ServerSession> inSessionManager)
    {
        TradeClientRpcService<ServerSession> tradeClientRpcService = new TradeClientRpcService<>();
        tradeClientRpcService.setAuthenticator(inAuthenticator);
        tradeClientRpcService.setSessionManager(inSessionManager);
        return tradeClientRpcService;
    }
    /**
     * Get the admin RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager&lt;ServerSession&gt;</code> value
     * @return an <code>AdminRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public AdminRpcService<ServerSession> getAdminRpcService(@Autowired Authenticator inAuthenticator,
                                                             @Autowired SessionManager<ServerSession> inSessionManager)
    {
        AdminRpcService<ServerSession> adminRpcService = new AdminRpcService<>();
        adminRpcService.setAuthenticator(inAuthenticator);
        adminRpcService.setSessionManager(inSessionManager);
        return adminRpcService;
    }
    /**
     * Get the data flow RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>DataFlowRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public DataFlowRpcService<ServerSession> getDataFlowRpcTradeService(@Autowired Authenticator inAuthenticator,
                                                                        @Autowired SessionManager<ServerSession> inSessionManager)
    {
        DataFlowRpcService<ServerSession> dataflowClientRpcService = new DataFlowRpcService<>();
        dataflowClientRpcService.setAuthenticator(inAuthenticator);
        dataflowClientRpcService.setSessionManager(inSessionManager);
        return dataflowClientRpcService;
    }
    /**
     * Get the market data RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>MarketDataRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public MarketDataRpcService<ServerSession> getMarketDataRpcTradeService(@Autowired Authenticator inAuthenticator,
                                                                            @Autowired SessionManager<ServerSession> inSessionManager)
    {
        MarketDataRpcService<ServerSession> marketDataClientRpcService = new MarketDataRpcService<>();
        marketDataClientRpcService.setAuthenticator(inAuthenticator);
        marketDataClientRpcService.setSessionManager(inSessionManager);
        return marketDataClientRpcService;
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
     * Get the symbol resolver service value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    @Bean
    public SymbolResolverService getSymbolResolverService()
    {
        IterativeSymbolResolver symbolResolverService = new IterativeSymbolResolver();
        symbolResolverService.setSymbolResolvers(Lists.newArrayList(new PatternSymbolResolver()));
        return symbolResolverService;
    }
    /**
     * Get the broker selector value.
     *
     * @return a <code>Selector</code> value
     */
    @Bean
    public Selector getBrokerSelector()
    {
        BasicSelector selector = new BasicSelector();
        return selector;
    }
    /**
     * Get the user attribute factory value.
     *
     * @return a <code>UserAttributeFactory</code> value
     */
    @Bean
    public UserAttributeFactory getUserAttributeFactory()
    {
        return new PersistentUserAttributeFactory();
    }
    /**
     * Get the user attribute service value.
     *
     * @return a <code>UserAttributeService</code> value
     */
    @Bean
    public UserAttributeService getUserAttributeService()
    {
        return new UserAttributeServiceImpl();
    }
    /**
     * 
     *
     *
     * @return
     */
    @Bean
    public PortUserProxy getWebServerPortUserProxy()
    {
        PortUserProxy proxy = new PortUserProxy();
        proxy.setDescription("Web Service");
        proxy.setPort(webServerPort);
        return proxy;
    }
    /**
     * provides data flows
     */
    @Autowired(required=false)
    private Collection<DataFlowProvider> dataFlowProviders = Lists.newArrayList();
    /**
     * message owner value
     */
    @Value("${metc.default.message.owner:trader}")
    private String defaultMessageOwner;
    /**
     * session life value in millis
     */
    @Value("${metc.session.life.mills:-1}")
    private long sessionLife;
    /**
     * RPC hostname
     */
    @Value("${metc.rpc.hostname:127.0.0.1}")
    private String rpcHostname;
    /**
     * RPC port
     */
    @Value("${metc.rpc.port:8998}")
    private int rpcPort;
    /**
     * web services port
     */
    @Value("${server.port:8999}")
    private int webServerPort;
}
