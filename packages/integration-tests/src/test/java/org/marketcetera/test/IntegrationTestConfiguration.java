package org.marketcetera.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.dao.PersistentUserAttributeFactory;
import org.marketcetera.admin.rpc.AdminRpcService;
import org.marketcetera.admin.service.UserAttributeService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.service.impl.UserAttributeServiceImpl;
import org.marketcetera.admin.service.impl.UserServiceImpl;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.brokers.service.InMemoryFixSessionProvider;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.dataflow.config.DataFlowProvider;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.MutableActiveFixSessionFactory;
import org.marketcetera.fix.MutableFixSession;
import org.marketcetera.fix.MutableFixSessionFactory;
import org.marketcetera.fix.ServerFixSessionFactory;
import org.marketcetera.fix.SessionSettingsGenerator;
import org.marketcetera.fix.impl.SimpleActiveFixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.fix.impl.SimpleServerFixSessionFactory;
import org.marketcetera.fix.provisioning.FixSessionsConfiguration;
import org.marketcetera.fix.provisioning.SimpleSessionCustomization;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.rpc.server.RpcServer;
import org.marketcetera.test.trade.Receiver;
import org.marketcetera.test.trade.Sender;
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
import org.marketcetera.trade.service.FieldSetterMessageModifier;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.TestBrokerSelector;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.grpc.BindableService;
import quickfix.MessageFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides configuration for {@link IntegrationTestBase} tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages={"org.marketcetera"})
public class IntegrationTestConfiguration
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
        ModuleManager.startModulesIfNecessary(moduleManager,
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
     * Get the test FIX sender.
     *
     * @param inMessageFactory a <code>quickfix.MessageFactory</code> value
     * @param inFixSettingsProviderFactory a <code>FixSettingsProviderFactory</code> value
     * @param inFixSessionsConfiguration a <code>FixSessionsConfiguration</code> value
     * @param inFixSessionFactory a <code>MutableFixSessionFactory</code> value
     * @return a <code>Sender</code> value
     */
    @Bean
    public Sender getSender(quickfix.MessageFactory inMessageFactory,
                            FixSettingsProviderFactory inFixSettingsProviderFactory,
                            FixSessionsConfiguration inFixSessionsConfiguration,
                            MutableFixSessionFactory inFixSessionFactory)
    {
        Sender sender = new Sender();
        sender.setFixSettingsProviderFactory(inFixSettingsProviderFactory);
        sender.setMessageFactory(inMessageFactory);
        Collection<FixSession> testSessions = Lists.newArrayList();
        FixSettingsProvider fixSettingsProvider = inFixSettingsProviderFactory.create();
        // examine the FIX sessions described for testing
        for(FixSessionsConfiguration.FixSessionDescriptor fixSessionsDescriptor : inFixSessionsConfiguration.getSessionDescriptors()) {
            Map<String,String> globalSettings = Maps.newHashMap(fixSessionsDescriptor.getSettings());
            // consider only the acceptor settings, we're going to create a mirror session for receiving the messages
            if(globalSettings.containsKey(quickfix.SessionFactory.SETTING_CONNECTION_TYPE)) {
                String value = globalSettings.get(quickfix.SessionFactory.SETTING_CONNECTION_TYPE);
                if(!quickfix.SessionFactory.ACCEPTOR_CONNECTION_TYPE.equals(value)) {
                    continue;
                }
            } else {
                throw new IllegalArgumentException("No CONNECTION_TYPE defined in " + globalSettings);
            }
            int testAcceptorPort = fixSettingsProvider.getAcceptorPort();
            // override the settings for the acceptors because we're creating initiators
            globalSettings.put(quickfix.Initiator.SETTING_SOCKET_CONNECT_PORT,
                               String.valueOf(testAcceptorPort));
            globalSettings.put(quickfix.SessionFactory.SETTING_CONNECTION_TYPE,
                               quickfix.SessionFactory.INITIATOR_CONNECTION_TYPE);
            globalSettings.put(quickfix.Session.SETTING_HEARTBTINT,
                               String.valueOf(1));
            for(FixSessionsConfiguration.Session fixSessionDescriptor : fixSessionsDescriptor.getSessions()) {
                Map<String,String> sessionSettings = Maps.newHashMap();
                sessionSettings.putAll(globalSettings);
                sessionSettings.putAll(fixSessionDescriptor.getSettings());
                MutableFixSession fixSession = inFixSessionFactory.create();
                fixSession.setAffinity(fixSessionDescriptor.getAffinity());
                fixSession.setBrokerId(fixSessionDescriptor.getBrokerId()+"-counter");
                if(fixSessionDescriptor.getMappedBrokerId() != null) {
                    fixSession.setMappedBrokerId(fixSessionDescriptor.getMappedBrokerId());
                }
                fixSession.setDescription(fixSessionDescriptor.getDescription());
                fixSession.setIsAcceptor(false);
                fixSession.setIsEnabled(true);
                fixSession.setHost(fixSettingsProvider.getAcceptorHost());
                fixSession.setPort(testAcceptorPort);
                fixSession.setName(fixSessionDescriptor.getName()+"-counter");
                // intentionally reversed
                SessionID sessionId = FIXMessageUtil.getReversedSessionId(new SessionID(sessionSettings.get(SessionSettings.BEGINSTRING),
                                                                                        sessionSettings.get(SessionSettings.SENDERCOMPID),
                                                                                        sessionSettings.get(SessionSettings.TARGETCOMPID)));
                fixSession.setSessionId(sessionId.toString());
                sessionSettings.put(SessionSettings.SENDERCOMPID,
                                    sessionId.getSenderCompID());
                sessionSettings.put(SessionSettings.TARGETCOMPID,
                                    sessionId.getTargetCompID());
                fixSession.getSessionSettings().putAll(sessionSettings);
                testSessions.add(fixSession);
            }
        }
        quickfix.SessionSettings quickfixSessionSettings = SessionSettingsGenerator.generateSessionSettings(testSessions,
                                                                                                            inFixSettingsProviderFactory);
        sender.setSessionSettings(quickfixSessionSettings);
        return sender;
    }
    /**
     * Get the test message receiver.
     *
     * @param inMessageFactory a <code>quickfix.MessageFactory</code> value
     * @param inFixSettingsProviderFactory a <code>FixSettingsProviderFactory</code> value
     * @param inFixSessionsConfiguration a <code>FixSessionsConfiguration</code> value
     * @param inFixSessionFactory a <code>MutableFixSessionFactory</code> value
     * @return a <code>Receiver</code> value
     */
    @Bean
    public Receiver getReceiver(quickfix.MessageFactory inMessageFactory,
                                FixSettingsProviderFactory inFixSettingsProviderFactory,
                                FixSessionsConfiguration inFixSessionsConfiguration,
                                MutableFixSessionFactory inFixSessionFactory)
    {
        Receiver receiver = new Receiver();
        receiver.setFixSettingsProviderFactory(inFixSettingsProviderFactory);
        receiver.setMessageFactory(inMessageFactory);
        Collection<FixSession> testSessions = Lists.newArrayList();
        FixSettingsProvider fixSettingsProvider = inFixSettingsProviderFactory.create();
        // examine the FIX sessions described for testing
        int testAcceptorPort = -1;
        for(FixSessionsConfiguration.FixSessionDescriptor fixSessionsDescriptor : inFixSessionsConfiguration.getSessionDescriptors()) {
            Map<String,String> globalSettings = Maps.newHashMap(fixSessionsDescriptor.getSettings());
            // consider only the initiator settings, we're going to create a mirror session for receiving the messages
            if(globalSettings.containsKey(quickfix.SessionFactory.SETTING_CONNECTION_TYPE)) {
                String value = globalSettings.get(quickfix.SessionFactory.SETTING_CONNECTION_TYPE);
                if(!quickfix.SessionFactory.INITIATOR_CONNECTION_TYPE.equals(value)) {
                    continue;
                }
            } else {
                throw new IllegalArgumentException("No CONNECTION_TYPE defined in " + globalSettings);
            }
            if(globalSettings.containsKey(quickfix.Initiator.SETTING_SOCKET_CONNECT_PORT)) {
                testAcceptorPort = Integer.valueOf(globalSettings.get(quickfix.Initiator.SETTING_SOCKET_CONNECT_PORT));
            } else {
                testAcceptorPort = fixSettingsProvider.getAcceptorPort()+1;
            }
            // override the settings for the initiators because we're creating acceptors
            globalSettings.put(quickfix.Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                               String.valueOf(testAcceptorPort));
            globalSettings.put(quickfix.SessionFactory.SETTING_CONNECTION_TYPE,
                               quickfix.SessionFactory.ACCEPTOR_CONNECTION_TYPE);
            for(FixSessionsConfiguration.Session fixSessionDescriptor : fixSessionsDescriptor.getSessions()) {
                Map<String,String> sessionSettings = Maps.newHashMap();
                sessionSettings.putAll(globalSettings);
                sessionSettings.putAll(fixSessionDescriptor.getSettings());
                MutableFixSession fixSession = inFixSessionFactory.create();
                fixSession.setAffinity(fixSessionDescriptor.getAffinity());
                fixSession.setBrokerId(fixSessionDescriptor.getBrokerId()+"-counter");
                if(fixSessionDescriptor.getMappedBrokerId() != null) {
                    fixSession.setMappedBrokerId(fixSessionDescriptor.getMappedBrokerId());
                }
                fixSession.setDescription(fixSessionDescriptor.getDescription());
                fixSession.setIsAcceptor(true);
                fixSession.setIsEnabled(true);
                fixSession.setHost(fixSettingsProvider.getAcceptorHost());
                fixSession.setPort(testAcceptorPort);
                fixSession.setName(fixSessionDescriptor.getName()+"-counter");
                // intentionally reversed
                SessionID sessionId = FIXMessageUtil.getReversedSessionId(new SessionID(sessionSettings.get(SessionSettings.BEGINSTRING),
                                                                                        sessionSettings.get(SessionSettings.SENDERCOMPID),
                                                                                        sessionSettings.get(SessionSettings.TARGETCOMPID)));
                fixSession.setSessionId(sessionId.toString());
                sessionSettings.put(SessionSettings.SENDERCOMPID,
                                    sessionId.getSenderCompID());
                sessionSettings.put(SessionSettings.TARGETCOMPID,
                                    sessionId.getTargetCompID());
                sessionSettings.put(quickfix.Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                    String.valueOf(testAcceptorPort));
                fixSession.getSessionSettings().putAll(sessionSettings);
                testSessions.add(fixSession);
            }
        }
        quickfix.SessionSettings quickfixSessionSettings = SessionSettingsGenerator.generateSessionSettings(testSessions,
                                                                                                            inFixSettingsProviderFactory);
        quickfixSessionSettings.getDefaultProperties().setProperty(quickfix.Acceptor.SETTING_SOCKET_ACCEPT_PORT,
                                                                   String.valueOf(testAcceptorPort));
        receiver.setSessionSettings(quickfixSessionSettings);
        return receiver;
    }
    /**
     * Get the active FIX session factory value.
     *
     * @return a <code>MutableActiveFixSessionFactory</code> value
     */
    @Bean
    public MutableActiveFixSessionFactory getActiveFixSessionFactory()
    {
        return new SimpleActiveFixSessionFactory();
    }
    /**
     * Get the FIX session factory value.
     *
     * @return a <code>MutableFixSessionFactory</code> value
     */
    @Bean
    public MutableFixSessionFactory getFixSessionFactory()
    {
        return new SimpleFixSessionFactory();
    }
    /**
     * Get the server FIX session factory value.
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
     * @param inOutgoingMessageLookupStrategy an <code>OutgoingMessageLookupStrategy</code> value
     * @param inDefaultOwnerStrategy a <code>DefaultOwnerStrategy</code> value
     * @return a <code>MessageOwnerService</code> value
     */
    @Bean
    public MessageOwnerService getMessageOwnerService(OutgoingMessageLookupStrategy inOutgoingMessageLookupStrategy,
                                                      DefaultOwnerStrategy inDefaultOwnerStrategy)
    {
        MessageOwnerServiceImpl messageOwnerService = new MessageOwnerServiceImpl();
        messageOwnerService.getIdentifyOwnerStrategies().add(inOutgoingMessageLookupStrategy);
        messageOwnerService.getIdentifyOwnerStrategies().add(inDefaultOwnerStrategy);
        return messageOwnerService;
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
     * Get the outgoing message lookup strategy.
     *
     * @return an <code>OutgoingMessageLookupStrategy</code> value
     */
    @Bean
    public OutgoingMessageLookupStrategy getOutgoingMessageLookupStrategy()
    {
        return new OutgoingMessageLookupStrategy();
    }
    /**
     * Get the admin client factory value.
     *
     * @return an <code>AdminRpcClientFactory</code> value
     */
    @Bean
    public AdminRpcClientFactory getAdminClientFactory()
    {
        return new AdminRpcClientFactory();
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
     * Get the admin RPC service.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     * @param inSessionManager&lt;MockSession&gt;</code> value
     * @return an <code>AdminRpcService&lt;MockSession&gt;</code> value
     */
    @Bean
    public AdminRpcService<MockSession> getAdminRpcService(@Autowired Authenticator inAuthenticator,
                                                           @Autowired SessionManager<MockSession> inSessionManager)
    {
        AdminRpcService<MockSession> adminRpcService = new AdminRpcService<>();
        adminRpcService.setAuthenticator(inAuthenticator);
        adminRpcService.setSessionManager(inSessionManager);
        return adminRpcService;
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
     * Get the client session factory value.
     *
     * @return a <code>MockSessionFactory</code> value
     */
    @Bean
    public MockSessionFactory getMockSessionFactory()
    {
        return new MockSessionFactory();
    }
    /**
     * Get the session manager value.
     *
     * @param inMockSessionFactory a <code>MockSessionFactory</code>value
     * @return a <code>SessionManager&lt;MockSession&gt;</code> value
     */
    @Bean
    public SessionManager<MockSession> getSessionManager(@Autowired MockSessionFactory inMockSessionFactory)
    {
        SessionManager<MockSession> sessionManager = new SessionManager<>(inMockSessionFactory,
                                                                          sessionLife);
        return sessionManager;
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
     * Get the test broker selector value.
     *
     * @return a <code>TestBrokerSelector</code> value
     */
    @Bean
    public TestBrokerSelector getTestBrokerSelector()
    {
        return new TestBrokerSelector();
    }
    /**
     * Get the session customization1 value.
     *
     * @return a <code>SessionCustomization</code>
     */
    @Bean("sessionCustomization1")
    public static SessionCustomization getSessionCustomization1()
    {
        SimpleSessionCustomization sessionCustomization = new SimpleSessionCustomization();
        sessionCustomization.setName("sessionCustomization1");
        FieldSetterMessageModifier modifier1 = new FieldSetterMessageModifier();
        modifier1.setField(10000);
        modifier1.setValue("bogus-value");
        FieldSetterMessageModifier modifier2 = new FieldSetterMessageModifier();
        modifier2.setField(10000);
        modifier2.setValue("10000-sessionCustomization1");
        FieldSetterMessageModifier modifier3 = new FieldSetterMessageModifier();
        modifier3.setField(10001);
        modifier3.setValue("10001-sessionCustomization1");
        sessionCustomization.setOrderModifiers(Lists.newArrayList(modifier1,modifier2,modifier3));
        return sessionCustomization;
    }
    /**
     * Get the session customization2 value.
     *
     * @return a <code>SessionCustomization</code>
     */
    @Bean("sessionCustomization2")
    public static SessionCustomization getSessionCustomization2()
    {
        SimpleSessionCustomization sessionCustomization = new SimpleSessionCustomization();
        sessionCustomization.setName("sessionCustomization2");
        FieldSetterMessageModifier modifier1 = new FieldSetterMessageModifier();
        modifier1.setField(10001);
        modifier1.setValue("bogus-value");
        FieldSetterMessageModifier modifier2 = new FieldSetterMessageModifier();
        modifier2.setField(10001);
        modifier2.setValue("10001-sessionCustomization2");
        FieldSetterMessageModifier modifier3 = new FieldSetterMessageModifier();
        modifier3.setField(10002);
        modifier3.setValue("10002-sessionCustomization2");
        sessionCustomization.setOrderModifiers(Lists.newArrayList(modifier1,modifier2,modifier3));
        return sessionCustomization;
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
     * RPC hostname
     */
    @Value("${metc.rpc.hostname:127.0.0.1}")
    private String rpcHostname = "127.0.0.1";
    /**
     * RPC port
     */
    @Value("${metc.rpc.port:8999}")
    private int rpcPort = 8999;
    /**
     * session life value in millis
     */
    @Value("${metc.session.life.mills}")
    private long sessionLife = SessionManager.INFINITE_SESSION_LIFESPAN;
    /**
     * provides data flows
     */
    @Autowired(required=false)
    private Collection<DataFlowProvider> dataFlowProviders = Lists.newArrayList();
}
