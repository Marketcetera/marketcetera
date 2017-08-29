package org.marketcetera.server;

import java.util.List;

import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.brokers.Selector;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.brokers.service.InMemoryFixSessionProvider;
import org.marketcetera.client.rpc.server.TradeClientRpcService;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.fix.FixDataRequest;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.server.session.ServerSession;
import org.marketcetera.server.session.ServerSessionFactory;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.BasicSelector;
import org.marketcetera.trade.TradeConstants;
import org.marketcetera.trade.impl.DefaultOwnerStrategy;
import org.marketcetera.trade.impl.OutgoingMessageLookupStrategy;
import org.marketcetera.trade.modules.OrderConverterModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessageCachingModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessagePersistenceModuleFactory;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.marketcetera.trading.rpc.TradingUtil;
import org.marketcetera.util.ws.stateful.Authenticator;
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
        moduleManager.createDataFlow(buildOutgoingOrderDataRequest(moduleManager));
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
        TradingUtil.setSymbolResolverService(symbolResolverService);
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
     * Build the outgoing order data flow.
     *
     * @param inModuleManager a <code>ModuleManager</code> value
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] buildOutgoingOrderDataRequest(ModuleManager inModuleManager)
    {
        startModulesIfNecessary(inModuleManager,
                                TransactionModuleFactory.INSTANCE_URN,
                                OrderConverterModuleFactory.INSTANCE_URN,
                                OutgoingMessageCachingModuleFactory.INSTANCE_URN,
                                OutgoingMessagePersistenceModuleFactory.INSTANCE_URN,
                                FixInitiatorModuleFactory.INSTANCE_URN);
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = HeadwaterModule.createHeadwaterModule(TradeConstants.outgoingDataFlowName,
                                                                       inModuleManager);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TransactionModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OrderConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessageCachingModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessagePersistenceModuleFactory.INSTANCE_URN));
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(false);
        fixDataRequest.setIncludeApp(true);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               fixDataRequest));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Start instance modules if necessary.
     *
     * @param inModuleManager a <code>ModuleManager</code> value
     * @param inInstanceUrns a <code>ModuleURN[]</code> value
     */
    private void startModulesIfNecessary(ModuleManager inModuleManager,
                                         ModuleURN...inInstanceUrns)
    {
        for(ModuleURN instanceUrn : inInstanceUrns) {
            if(!inModuleManager.getModuleInfo(instanceUrn).getState().isStarted()) {
                inModuleManager.start(instanceUrn);
            }
        }
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
