package org.marketcetera.server;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.ServerSession;

import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.auth.DBAuthenticator;
import org.marketcetera.admin.dao.PersistentPermissionFactory;
import org.marketcetera.admin.dao.PersistentRoleFactory;
import org.marketcetera.admin.dao.PersistentUserAttributeFactory;
import org.marketcetera.admin.provisioning.ProvisioningAgent;
import org.marketcetera.admin.rpc.AdminRpcService;
import org.marketcetera.admin.service.UserAttributeService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserAttributeServiceImpl;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.admin.user.PersistentUserFactory;
import org.marketcetera.brokers.BrokerSelector;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.client.rpc.server.TradeRpcService;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.cluster.SimpleClusterDataFactory;
import org.marketcetera.cluster.SimpleClusterService;
import org.marketcetera.cluster.rpc.ClusterRpcService;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.XmlService;
import org.marketcetera.dataflow.server.rpc.DataFlowRpcService;
import org.marketcetera.eventbus.server.EsperEngine;
import org.marketcetera.eventbus.server.EventBusEsperConnector;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.fix.ServerFixSessionFactory;
import org.marketcetera.fix.dao.PersistentFixSessionFactory;
import org.marketcetera.fix.dao.PersistentFixSessionProvider;
import org.marketcetera.fix.impl.SimpleActiveFixSessionFactory;
import org.marketcetera.fix.impl.SimpleServerFixSessionFactory;
import org.marketcetera.fix.rpc.FixAdminRpcService;
import org.marketcetera.fix.store.HibernateMessageStoreConfiguration;
import org.marketcetera.marketdata.rpc.server.MarketDataRpcService;
import org.marketcetera.metrics.MetricServiceDbReporter;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.quickfix.QuickFIXSenderImpl;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.strategy.StrategyRpcServer;
import org.marketcetera.strategy.StrategyService;
import org.marketcetera.strategy.StrategyServiceImpl;
import org.marketcetera.strategy.dao.PersistentStrategyInstanceFactory;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.AverageFillPriceFactory;
import org.marketcetera.trade.BasicSelector;
import org.marketcetera.trade.SimpleAverageFillPriceFactory;
import org.marketcetera.trade.event.connector.IncomingTradeMessageBroadcastConnector;
import org.marketcetera.trade.event.connector.IncomingTradeMessageConverterConnector;
import org.marketcetera.trade.event.connector.IncomingTradeMessagePersistenceConnector;
import org.marketcetera.trade.event.connector.OrderConverterConnector;
import org.marketcetera.trade.event.connector.OutgoingMessageCachingConnector;
import org.marketcetera.trade.event.connector.OutgoingMessagePersistenceConnector;
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
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/* $License$ */

/**
 * Application entry point for the community DARE application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@EnableTransactionManagement
@EntityScan(basePackages={"org.marketcetera","com.marketcetera"})
@SpringBootApplication(scanBasePackages={"org.marketcetera","com.marketcetera"})
@EnableJpaRepositories(basePackages={"org.marketcetera","com.marketcetera"})
public class DareApplication
{
    /**
     * Main application entry.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(DareApplication.class,
                              inArgs);
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Core version {} revision {} timestamp {} repository {} path {}",
                              org.marketcetera.core.Version.pomversion,
                              org.marketcetera.core.Version.build_number,
                              org.marketcetera.core.Version.build_time,
                              org.marketcetera.core.Version.build_repository,
                              org.marketcetera.core.Version.build_path);
    }
    /**
     * Get the strategy service bean.
     *
     * @return a <code>StrategyService</code> value
     */
    @Bean
    public StrategyService getStrategyService()
    {
        // some bizarre something is making this be explicitly declared here even though it's a Component
        return new StrategyServiceImpl();
    }
    /**
     * Get the provisioning agent bean.
     *
     * @return a <code>ProvisioningAgent</code> value
     */
    @Bean
    public ProvisioningAgent getProvisioningAgent()
    {
        ProvisioningAgent provisioningAgent = new ProvisioningAgent();
        provisioningAgent.setProvisioningDirectory(provisioningDirectory);
        return provisioningAgent;
    }
    /**
     * Get the XmlService value.
     *
     * @return an <code>XmlService</code> value
     * @throws ClassNotFoundException if a context class path cannot be resolved
     */
    @Bean
    public XmlService getXmlService()
            throws ClassNotFoundException
    {
        XmlService xmlService = new XmlService();
        List<Class<?>> contextPathClassEntries = Lists.newArrayList();
        for(String contextPathEntry : contextPathClasses) {
            contextPathClassEntries.add(Class.forName(contextPathEntry));
        }
        SLF4JLoggerProxy.info(this,
                              "Using the");
        xmlService.setContextPath(contextPathClassEntries);
        return xmlService;
    }
    /**
     * Get the strategy instance factory value.
     *
     * @return a <code>StrategyInstanceFactory</code> value
     */
    @Bean
    public PersistentStrategyInstanceFactory getStrategyInstanceFactory()
    {
        return new PersistentStrategyInstanceFactory();
    }
    /**
     * Get the average fill price factory value.
     *
     * @return an <code>AverageFillPriceFactory</code> value
     */
    @Bean
    public AverageFillPriceFactory getAverageFillPriceFactory()
    {
        return new SimpleAverageFillPriceFactory();
    }
    /**
     * Get the message store configuration value.
     *
     * @return a <code>HibernateMessageStoreConfiguration</code> value
     */
    @Bean
    public HibernateMessageStoreConfiguration getMessageStoreConfiguration()
    {
        return new HibernateMessageStoreConfiguration();
    }
    /**
     * Get the user service bean.
     *
     * @return a <code>UserService</code> value
     */
    @Bean
    public UserService getUserService()
    {
        return new UserServiceImpl();
    }
    /**
     * Get the FIX session provider bean.
     *
     * @return a <code>FixSessionProvider</code> value
     */
    @Bean
    public FixSessionProvider getFixSessionProvider()
    {
        return new PersistentFixSessionProvider();
    }
    /**
     * Get the message factory bean.
     *
     * @return a <code>quickfix.MessageFactory</code> value
     */
    @Bean
    public quickfix.MessageFactory getMessageFactory()
    {
        return new quickfix.DefaultMessageFactory();
    }
    /**
     * Get the cluster service bean.
     *
     * @return a <code>ClusterService</code> value
     */
    @Bean
    public ClusterService getClusterService()
    {
        return new SimpleClusterService();
    }
    /**
     * Get the cluster data factory bean.
     *
     * @return a <code>ClusterDataFactory</code> value
     */
    @Bean
    public ClusterDataFactory getClusterDataFactory()
    {
        return new SimpleClusterDataFactory();
    }
    /**
     * Get the mutable FIX session factory bean.
     *
     * @return a <code>MutableFixSessionFactory</code> value
     */
    @Bean
    public MutableFixSessionFactory getMutableFixSessionFactory()
    {
        return new PersistentFixSessionFactory();
    }
    /**
     * Get the mutable active FIX session factory bean.
     *
     * @return a <code>MutableActiveFixSessionFactory</code> value
     */
    @Bean
    public MutableActiveFixSessionFactory getMutableActiveFixSessionFactory()
    {
        return new SimpleActiveFixSessionFactory();
    }
    /**
     * Get the server FIX session factory bean.
     *
     * @return a <code>ServerFixSessionFactory</code> value
     */
    @Bean
    public ServerFixSessionFactory getServerFixSessionFactory()
    {
        return new SimpleServerFixSessionFactory();
    }
    /**
     * Get the message owner service value.
     *
     * @return a <code>MessageOwnerService</code> value
     */
    @Bean
    public MessageOwnerService getMessageOwnerService()
    {
        return new MessageOwnerServiceImpl();
    }
    /**
     * Get the QFJ sender implementation for DARE.
     *
     * @return a <code>QuickFIXSender</code> value
     */
    @Bean
    public QuickFIXSender getQuickFixSender()
    {
        return new QuickFIXSenderImpl();
    }
    /**
     * Get the metric service reporter value.
     *
     * @return a <code>MetricServiceDbReporter</code> value
     */
    @Bean
    public MetricServiceDbReporter getMetricServiceReporter()
    {
        MetricServiceDbReporter metricServiceReporter = new MetricServiceDbReporter();
        metricServiceReporter.setReportInterval(metricServiceLogReporterInterval);
        return metricServiceReporter;
    }
    /**
     * Create the Swagger API component.
     *
     * @return a <code>Docket</code> value
     */
    @Bean
    public Docket api()
    { 
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/matp/*"))
                .build()
                .apiInfo(apiInfo());
    }
    /**
     * Get the port user proxy for the embedded web server.
     *
     * @return a <code>PortUserProxy</code> value
     */
    @Bean
    public PortUserProxy getEmbeddedWebServerPortUserProxy()
    {
        PortUserProxy proxy = new PortUserProxy();
        proxy.setPort(webServerPort);
        proxy.setDescription("DARE Web Server");
        return proxy;
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
        rpcServer.setHostname(serverHostname);
        rpcServer.setPort(rpcPort);
        if(inServiceSpecs != null) {
            for(BindableService service : inServiceSpecs) {
                rpcServer.getServerServiceDefinitions().add(service);
            }
        }
        return rpcServer;
    }
    /**
     * Get the admin RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
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
     * Get the Trade RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>TradeRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public TradeRpcService<ServerSession> getTradeRpcService(@Autowired Authenticator inAuthenticator,
                                                             @Autowired SessionManager<ServerSession> inSessionManager)
    {
        TradeRpcService<ServerSession> tradeRpcService = new TradeRpcService<>();
        tradeRpcService.setAuthenticator(inAuthenticator);
        tradeRpcService.setSessionManager(inSessionManager);
        return tradeRpcService;
    }
    /**
     * Get the Strategy RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionbManager&lt;ServerSession&gt;</code> value
     * @return a <code>TradeRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public StrategyRpcServer<ServerSession> getStrategyRpcService(@Autowired Authenticator inAuthenticator,
                                                                  @Autowired SessionManager<ServerSession> inSessionManager)
    {
        StrategyRpcServer<ServerSession> strategyRpcServer = new StrategyRpcServer<>();
        strategyRpcServer.setAuthenticator(inAuthenticator);
        strategyRpcServer.setSessionManager(inSessionManager);
        return strategyRpcServer;
    }
    /**
     * Get the Fix Admin RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>FixAdminRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public FixAdminRpcService<ServerSession> getFixAdminRpcService(@Autowired Authenticator inAuthenticator,
                                                                   @Autowired SessionManager<ServerSession> inSessionManager)
    {
        FixAdminRpcService<ServerSession> fixAdminRpcService = new FixAdminRpcService<>();
        fixAdminRpcService.setAuthenticator(inAuthenticator);
        fixAdminRpcService.setSessionManager(inSessionManager);
        return fixAdminRpcService;
    }
    /**
     * Get the Data Flow RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>DataFlowRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public DataFlowRpcService<ServerSession> getDataFlowRpcService(@Autowired Authenticator inAuthenticator,
                                                                   @Autowired SessionManager<ServerSession> inSessionManager)
    {
        DataFlowRpcService<ServerSession> dataFlowRpcService = new DataFlowRpcService<>();
        dataFlowRpcService.setAuthenticator(inAuthenticator);
        dataFlowRpcService.setSessionManager(inSessionManager);
        return dataFlowRpcService;
    }
    /**
     * Get the Market Data RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>MarketDataRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public MarketDataRpcService<ServerSession> getMarketDataRpcService(@Autowired Authenticator inAuthenticator,
                                                                       @Autowired SessionManager<ServerSession> inSessionManager)
    {
        MarketDataRpcService<ServerSession> marketDataRpcService = new MarketDataRpcService<>();
        marketDataRpcService.setAuthenticator(inAuthenticator);
        marketDataRpcService.setSessionManager(inSessionManager);
        return marketDataRpcService;
    }
    /**
     * Get the Cluster RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager a <code>SessionManager&lt;ServerSession&gt;</code> value
     * @return a <code>ClusterRpcService&lt;ServerSession&gt;</code> value
     */
    @Bean
    public ClusterRpcService<ServerSession> getClusterRpcService(@Autowired Authenticator inAuthenticator,
                                                                 @Autowired SessionManager<ServerSession> inSessionManager)
    {
        ClusterRpcService<ServerSession> clusterRpcService = new ClusterRpcService<>();
        clusterRpcService.setAuthenticator(inAuthenticator);
        clusterRpcService.setSessionManager(inSessionManager);
        return clusterRpcService;
    }
    /**
     * Get the module manager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    @Bean
    public ModuleManager getModuleManager()
    {
        ModuleManager moduleManager = ModuleManager.getInstance();
        if(moduleManager == null) {
            moduleManager = new ModuleManager();
            moduleManager.init();
        }
        return moduleManager;
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
        symbolResolverService.getSymbolResolvers().add(new PatternSymbolResolver());
        return symbolResolverService;
    }
    /**
     * Get the authenticator service.
     *
     * @return an <code>Authenticator</code> value
     */
    @Bean
    public Authenticator getAuthenticator()
    {
        return new DBAuthenticator();
    }
    /**
     * Get the session manager service.
     *
     * @return a <code>SessionManager&lt;ServerSession&gt;</code> value
     */
    @Bean
    public SessionManager<ServerSession> getSessionManager()
    {
        return new SessionManager<ServerSession>();
    }
    /**
     * Get the user attribute factory value.
     *
     * @return a <code>UserAttribute</code> value
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
     * Get the default broker selector to use.
     *
     * @return a <code>Selector</code> value
     */
    @Bean
    public BrokerSelector getBrokerSelector()
    {
        return new BasicSelector();
    }
    // begin event connectors
    /**
     * Get the order converter connector value.
     *
     * @return an <code>OrderConverterConnector</code> value
     */
    @Bean
    public OrderConverterConnector getOrderConverterConnector()
    {
        return new OrderConverterConnector();
    }
    /**
     * Get the outgoing message caching connector value.
     *
     * @return an <code>OutgoingMessageCachingConnector</code> value
     */
    @Bean
    public OutgoingMessageCachingConnector getOutgoingMessageCachingConnector()
    {
        return new OutgoingMessageCachingConnector();
    }
    /**
     * Get the outgoing message persistence connector value.
     *
     * @return an <code>OutgoingMessagePersistenceConnector</code> value
     */
    @Bean
    public OutgoingMessagePersistenceConnector getOutgoingMessagePersistenceConnector()
    {
        return new OutgoingMessagePersistenceConnector();
    }
    /**
     * Get the incoming trade message converter connector value.
     *
     * @return an <code>IncomingTradeMessageConverterConnector</code> value
     */
    @Bean
    public IncomingTradeMessageConverterConnector getIncomingTradeMessageConverterConnector()
    {
        return new IncomingTradeMessageConverterConnector();
    }
    /**
     * Get the incoming trade message persistence connector value.
     *
     * @return an <code>IncomingTradeMessagePersistenceConnector</code> value
     */
    @Bean
    public IncomingTradeMessagePersistenceConnector getIncomingTradeMessagePersistenceConnector()
    {
        return new IncomingTradeMessagePersistenceConnector();
    }
    /**
     * Get the IncomingTradeMessageBroadcastConnector value.
     *
     * @return an <code>IncomingTradeMessageBroadcastConnector</code> value
     */
    @Bean
    public IncomingTradeMessageBroadcastConnector getIncomingTradeMessageBroadcastConnector()
    {
        return new IncomingTradeMessageBroadcastConnector();
    }
    /**
     * Get the Esper engine value.
     *
     * @return an <code>EsperEngine</code> value
     */
    @Bean
    public EsperEngine getEsperEngine()
    {
        EsperEngine esperEngine = new EsperEngine();
        return esperEngine;
    }
    /**
     * Get the Event Bus &lt;-&gt; Esper connector value.
     *
     * @return an <code>EventBusEsperConnector</code> value
     */
    @Bean
    public EventBusEsperConnector getEventBusEsperConnector()
    {
        EventBusEsperConnector eventBusEsperConnector = new EventBusEsperConnector();
        return eventBusEsperConnector;
    }
    /**
     * Get the permission factory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    @Bean
    public PermissionFactory getPermissionFactory()
    {
        return new PersistentPermissionFactory();
    }
    /**
     * Get the role factory value.
     *
     * @return a <code>RoleFactory</code> value
     */
    @Bean
    public RoleFactory getRoleFactory()
    {
        return new PersistentRoleFactory();
    }
    /**
     * Get the user factory value.
     *
     * @return a <code>UserFactory</code> value
     */
    @Bean
    public UserFactory getUserFactory()
    {
        return new PersistentUserFactory();
    }
    /**
     * Get the API info (REST Swagger) for DARE.
     *
     * @return an <code>ApiInfo</code> value
     */
    private ApiInfo apiInfo()
    {
        return new ApiInfo(
          "Marketcetera Automated Trading Engine REST API", 
          "REST API for MATP", 
          "API TOS", 
          "Terms of service", 
          new Contact("Colin DuPlantis", 
                      "www.marketcetera.com",
                      "info@marketcetera.com"), 
          "License of API",
          "API license URL",
          Collections.emptyList());
    }
    /**
     * web services port
     */
    @Value("${server.port}")
    private int webServerPort;
    /**
     * server hostname
     */
    @Value("${metc.ws.hostname:0.0.0.0}")
    private String serverHostname;
    /**
     * RPC services port
     */
    @Value("${metc.rpc.port}")
    private int rpcPort;
    /**
     * interval at which to report metrics
     */
    @Value("${metc.metric.service.log.reporter.interval:10}")
    private int metricServiceLogReporterInterval;
    /**
     * context path classes used to marshal and unmarshal XML values
     */
    @Value("${metc.xml.context.path.classes}")
    private List<String> contextPathClasses;
    /**
     * provisioning directory base
     */
    @Value("${metc.provisioning.directory:./instances/provisioning}")
    private String provisioningDirectory;
}
