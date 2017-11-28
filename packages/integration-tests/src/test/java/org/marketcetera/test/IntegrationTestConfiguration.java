package org.marketcetera.test;

import java.util.Collection;
import java.util.Map;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.dataflow.config.DataFlowProvider;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.fix.FixSettingsProviderFactory;
import org.marketcetera.fix.SessionSettingsGenerator;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.fix.provisioning.FixSessionsConfiguration;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
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
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.impl.MessageOwnerServiceImpl;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
     * @param inFixSessionFactory a <code>FixSessionFactory</code> value
     * @return a <code>Sender</code> value
     */
    @Bean
    public Sender getSender(quickfix.MessageFactory inMessageFactory,
                            FixSettingsProviderFactory inFixSettingsProviderFactory,
                            FixSessionsConfiguration inFixSessionsConfiguration,
                            FixSessionFactory inFixSessionFactory)
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
                FixSession fixSession = inFixSessionFactory.create();
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
     * @param inFixSessionFactory a <code>FixSessionFactory</code> value
     * @return a <code>Receiver</code> value
     */
    @Bean
    public Receiver getReceiver(quickfix.MessageFactory inMessageFactory,
                                FixSettingsProviderFactory inFixSettingsProviderFactory,
                                FixSessionsConfiguration inFixSessionsConfiguration,
                                FixSessionFactory inFixSessionFactory)
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
                FixSession fixSession = inFixSessionFactory.create();
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
     * provides data flows
     */
    @Autowired(required=false)
    private Collection<DataFlowProvider> dataFlowProviders = Lists.newArrayList();
}
