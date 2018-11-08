package com.marketcetera.ors;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.marketcetera.client.BrokerStatusListener;
import org.marketcetera.client.BrokerStatusPublisher;
import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.jms.DataEnvelope;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.client.jms.ReceiveOnlyHandler;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.fix.FixSettingsProvider;
import org.marketcetera.core.fix.FixSettingsProviderFactory;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.NotificationExecutor;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.quickfix.SessionStatus;
import org.marketcetera.quickfix.SessionStatusListener;
import org.marketcetera.quickfix.SessionStatusPublisher;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.marketcetera.core.EnterprisePlatformServices;
import com.marketcetera.fix.ClusteredBrokerStatus;
import com.marketcetera.fix.FixSession;
import com.marketcetera.fix.FixSessionListener;
import com.marketcetera.fix.FixSessionStatus;
import com.marketcetera.fix.SessionService;
import com.marketcetera.keytools.KeyReader;
import com.marketcetera.matp.cluster.ClusterActivateWorkUnit;
import com.marketcetera.matp.cluster.ClusterData;
import com.marketcetera.matp.cluster.ClusterWorkUnit;
import com.marketcetera.matp.cluster.ClusterWorkUnitType;
import com.marketcetera.matp.cluster.ClusterWorkUnitUid;
import com.marketcetera.matp.service.ClusterService;
import com.marketcetera.ors.brokers.BrokerService;
import com.marketcetera.ors.brokers.FixSessionRestoreExecutor;
import com.marketcetera.ors.dao.DatabaseVersionMismatch;
import com.marketcetera.ors.dao.PersistentReportDao;
import com.marketcetera.ors.dao.ReportService;
import com.marketcetera.ors.dao.SystemInfoService;
import com.marketcetera.ors.filters.MessageFilter;
import com.marketcetera.ors.history.ReportHistoryServices;
import com.marketcetera.ors.history.RootOrderIdFactory;
import com.marketcetera.ors.info.SystemInfo;
import com.marketcetera.ors.info.SystemInfoImpl;
import com.marketcetera.ors.mbeans.ORSAdmin;
import com.marketcetera.ors.outgoingorder.OutgoingMessageService;
import com.marketcetera.ors.ws.ClientSession;
import com.marketcetera.ors.ws.ClientSessionFactory;

import quickfix.ConfigError;
import quickfix.DefaultSessionFactory;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;

/* $License$ */

/**
 * Routes orders to order destination and maintains FIX sessions.
 *
 * @author tlerios@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 1.0.0
 * @version $Id: OrderRoutingSystem.java 17336 2017-08-01 20:14:09Z colin $
 */
@ClusterWorkUnit(id="MATP.DARE",type=ClusterWorkUnitType.SINGLETON_RUNTIME)
@ClassVersion("$Id: OrderRoutingSystem.java 17336 2017-08-01 20:14:09Z colin $")
public class OrderRoutingSystem
        implements BrokerStatusPublisher,BrokerStatusListener,SessionStatusPublisher,SessionStatusListener,FixSessionListener
{
    /**
     * Create a new OrderRoutingSystem instance.
     */
    public OrderRoutingSystem()
    {
        instance = this;
    }
    /**
     * Get the idFactory value.
     *
     * @return an <code>IDFactory</code> value
     */
    public IDFactory getIdFactory()
    {
        return idFactory;
    }
    /**
     * Sets the idFactory value.
     *
     * @param inIdFactory an <code>IDFactory</code> value
     */
    public void setIdFactory(IDFactory inIdFactory)
    {
        idFactory = inIdFactory;
    }
    /**
     * Get the jmsManager value.
     *
     * @return a <code>JmsManager</code> value
     */
    public JmsManager getJmsManager()
    {
        return jmsManager;
    }
    /**
     * Sets the jmsManager value.
     *
     * @param inJmsManager a <code>JmsManager</code> value
     */
    public void setJmsManager(JmsManager inJmsManager)
    {
        jmsManager = inJmsManager;
    }
    /**
     * Get the reportHistoryServices value.
     *
     * @return a <code>ReportHistoryServices</code> value
     */
    public ReportHistoryServices getReportHistoryServices()
    {
        return reportHistoryServices;
    }
    /**
     * Sets the reportHistoryServices value.
     *
     * @param inReportHistoryServices a <code>ReportHistoryServices</code> value
     */
    public void setReportHistoryServices(ReportHistoryServices inReportHistoryServices)
    {
        reportHistoryServices = inReportHistoryServices;
    }
    /**
     * Get the replyPersister value.
     *
     * @return a <code>ReplyPersister</code> value
     */
    public ReplyPersister getReplyPersister()
    {
        return replyPersister;
    }
    /**
     * Sets the replyPersister value.
     *
     * @param inReplyPersister a <code>ReplyPersister</code> value
     */
    public void setReplyPersister(ReplyPersister inReplyPersister)
    {
        replyPersister = inReplyPersister;
    }
    /**
     * Get the systemInfo value.
     *
     * @return a <code>SystemInfoImpl</code> value
     */
    public SystemInfoImpl getSystemInfo()
    {
        return systemInfo;
    }
    /**
     * Sets the systemInfo value.
     *
     * @param inSystemInfo a <code>SystemInfoImpl</code> value
     */
    public void setSystemInfo(SystemInfoImpl inSystemInfo)
    {
        systemInfo = inSystemInfo;
    }
    /**
     * Get the userManager value.
     *
     * @return a <code>UserManager</code> value
     */
    public UserManager getUserManager()
    {
        return userManager;
    }
    /**
     * Sets the userManager value.
     *
     * @param inUserManager a <code>UserManager</code> value
     */
    public void setUserManager(UserManager inUserManager)
    {
        userManager = inUserManager;
    }
    /**
     * Get the clientSessionFactory value.
     *
     * @return a <code>ClientSessionFactory</code> value
     */
    public ClientSessionFactory getClientSessionFactory()
    {
        return clientSessionFactory;
    }
    /**
     * Sets the clientSessionFactory value.
     *
     * @param inClientSessionFactory a <code>ClientSessionFactory</code> value
     */
    public void setClientSessionFactory(ClientSessionFactory inClientSessionFactory)
    {
        clientSessionFactory = inClientSessionFactory;
    }
    /**
     * Get the sessionManager value.
     *
     * @return a <code>SessionManager&lt;ClientSession&gt;</code> value
     */
    public SessionManager<ClientSession> getSessionManager()
    {
        return sessionManager;
    }
    /**
     * Sets the sessionManager value.
     *
     * @param inSessionManager a <code>SessionManager&lt;ClientSession&gt;</code> value
     */
    public void setSessionManager(SessionManager<ClientSession> inSessionManager)
    {
        sessionManager = inSessionManager;
    }
    /**
     * Get the server value.
     *
     * @return a <code>Server&lt;ClientSession&gt;</code> value
     */
    public Server<ClientSession> getServer()
    {
        return server;
    }
    /**
     * Sets the server value.
     *
     * @param inServer a <code>Server&lt;ClientSession&gt;</code> value
     */
    public void setServer(Server<ClientSession> inServer)
    {
        server = inServer;
    }
    /**
     * Get the service value.
     *
     * @return a <code>Service</code> value
     */
    public Service getService()
    {
        return service;
    }
    /**
     * Sets the service value.
     *
     * @param inService a <code>Service</code> value
     */
    public void setService(Service inService)
    {
        service = inService;
    }
    /**
     * Get the quickFixSender value.
     *
     * @return a <code>QuickFIXSender</code> value
     */
    public QuickFIXSender getQuickFixSender()
    {
        return quickFixSender;
    }
    /**
     * Sets the quickFixSender value.
     *
     * @param inQuickFixSender a <code>QuickFIXSender</code> value
     */
    public void setQuickFixSender(QuickFIXSender inQuickFixSender)
    {
        quickFixSender = inQuickFixSender;
    }
    /**
     * Get the requestHandler value.
     *
     * @return a <code>ReceiveOnlyHandler&lt;DataEnvelope&gt;</code> value
     */
    public ReceiveOnlyHandler<DataEnvelope> getRequestHandler()
    {
        return requestHandler;
    }
    /**
     * Sets the requestHandler value.
     *
     * @param inRequestHandler a <code>ReceiveOnlyHandler&lt;DataEnvelope&gt;</code> value
     */
    public void setRequestHandler(ReceiveOnlyHandler<DataEnvelope> inRequestHandler)
    {
        requestHandler = inRequestHandler;
    }
    /**
     * Get the product key value.
     *
     * @return a <code>String</code> value
     */
    public String getProductKey()
    {
        return productKey;
    }
    /**
     * Sets the product key value.
     *
     * @param inProductKey a <code>String</code> value
     */
    public void setProductKey(String inProductKey)
    {
        productKey = inProductKey;
    }
    /**
     * Get the maxExecutionPools value.
     *
     * @return an <code>int</code> value
     */
    public int getMaxExecutionPools()
    {
        return maxExecutionPools;
    }
    /**
     * Sets the maxExecutionPools value.
     *
     * @param inMaxExecutionPools an <code>int</code> value
     */
    public void setMaxExecutionPools(int inMaxExecutionPools)
    {
        maxExecutionPools = inMaxExecutionPools;
        if(qfApp != null) {
            qfApp.setMaxExecutionPools(maxExecutionPools);
        }
    }
    /**
     * Get the executionPoolDelay value.
     *
     * @return a <code>long</code> value
     */
    public long getExecutionPoolDelay()
    {
        return executionPoolDelay;
    }
    /**
     * Sets the executionPoolDelay value.
     *
     * @param inExecutionPoolDelay a <code>long</code> value
     */
    public void setExecutionPoolDelay(long inExecutionPoolDelay)
    {
        executionPoolDelay = inExecutionPoolDelay;
    }
    /**
     * Get the executionPoolTtl value.
     *
     * @return a <code>long</code> value
     */
    public long getExecutionPoolTtl()
    {
        return executionPoolTtl;
    }
    /**
     * Sets the executionPoolTtl value.
     *
     * @param inExecutionPoolTtl a <code>long</code> value
     */
    public void setExecutionPoolTtl(long inExecutionPoolTtl)
    {
        executionPoolTtl = inExecutionPoolTtl;
    }
    /**
     * Gets the <code>OrderReceiver</code> value.
     *
     * @return an <code>OrderReceiver</code> value
     */
    public ReportReceiver getOrderReceiver()
    {
        return qfApp;
    }
    /**
     * Get the reportDao value.
     *
     * @return a <code>PersistentReportDao</code> value
     */
    public PersistentReportDao getReportDao()
    {
        return reportDao;
    }
    /**
     * Sets the reportDao value.
     *
     * @param inReportDao a <code>PersistentReportDao</code> value
     */
    public void setReportDao(PersistentReportDao inReportDao)
    {
        reportDao = inReportDao;
    }
    /**
     * Get the qfApp value.
     *
     * @return a <code>QuickFIXApplication</code> value
     */
    public QuickFIXApplication getQfApp()
    {
        return qfApp;
    }
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(replyPersister);
        Validate.notNull(reportHistoryServices);
        Validate.notNull(jmsManager);
        Validate.notNull(systemInfo);
        Validate.notNull(userManager);
        Validate.notNull(clientSessionFactory);
        Validate.notNull(sessionManager);
        Validate.notNull(server);
        Validate.notNull(service);
        Validate.notNull(quickFixSender);
        Validate.notNull(idFactory);
        Validate.notNull(requestHandler);
        Validate.notNull(clusterService);
        Validate.notNull(reportService);
        Validate.notNull(brokerService);
        // check database version
        verifyDatabaseVersion();
        // Create system information.
        systemInfo.setValue(SystemInfo.HISTORY_SERVICES,
                            reportHistoryServices);
        reportHistoryServices.init(idFactory,
                                   jmsManager,
                                   replyPersister);
        // Set dictionary for all QuickFIX/J messages we generate.
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryName()));
        // Initiate web services
        userManager.setSessionManager(sessionManager);
        server.publish(service,
                       Service.class);
        clusterData = clusterService.getInstanceData();
        int instanceId = clusterData.getInstanceNumber();
        clusterWorkUnitUid = OrderRoutingSystem.class.getSimpleName() + "-" + instanceId;
        brokerService.addFixSessionListener(this);
        statusUpdater = Executors.newSingleThreadScheduledExecutor();
        backupStatusTask = new Runnable() {
            @Override
            public void run()
            {
                try {
                    reportBackupStatus();
                    SLF4JLoggerProxy.debug(OrderRoutingSystem.this,
                                           "{} successfully completed backup status notification",
                                           this);
                } catch (Exception e) {
                    SLF4JLoggerProxy.debug(OrderRoutingSystem.this,
                                           e,
                                           "{} failed to report backup status, will try again",
                                           this);
                    statusUpdater.schedule(backupStatusTask,
                                           1000,
                                           TimeUnit.MILLISECONDS);
                }
            }
        };
        statusUpdater.schedule(backupStatusTask,
                               1000,
                               TimeUnit.MILLISECONDS);
        if(notificationExecutor != null) {
            notificationExecutor.notify(Notification.low("DARE Started",
                                                         "DARE Started at " + new DateTime(),
                                                         OrderRoutingSystem.class.getSimpleName()));
        }
        Messages.APP_STARTED.info(this);
    }
    /**
     * Gets the cluster work unit UID.
     *
     * @return a <code>String</code> value
     */
    @ClusterWorkUnitUid
    public String getClusterWorkUnitUid()
    {
        return clusterWorkUnitUid;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getClusterWorkUnitUid();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.BrokerStatusPublisher#addBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusListeners.add(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.BrokerStatusPublisher#removeBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        brokerStatusListeners.remove(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.client.brokers.BrokerStatus)
     */
    @Override
    public void receiveBrokerStatus(BrokerStatus inStatus)
    {
        for(BrokerStatusListener listener : brokerStatusListeners) {
            listener.receiveBrokerStatus(inStatus);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.SessionStatusListener#receiveSessionStatus(org.marketcetera.quickfix.SessionStatusNotification)
     */
    @Override
    public void receiveSessionStatus(SessionStatus inStatus)
    {
        for(SessionStatusListener listener : sessionStatusListeners) {
            listener.receiveSessionStatus(inStatus);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.SessionStatusPublisher#addSessionStatusListener(org.marketcetera.quickfix.SessionStatusListener)
     */
    @Override
    public void addSessionStatusListener(SessionStatusListener inSessionStatusListener)
    {
        sessionStatusListeners.add(inSessionStatusListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.SessionStatusPublisher#removeSessionStatusListener(org.marketcetera.quickfix.SessionStatusListener)
     */
    @Override
    public void removeSessionStatusListener(SessionStatusListener inListener)
    {
        sessionStatusListeners.remove(inListener);
    }
    /**
     * Activates the object and makes it ready for use in a clustered environment.
     *
     * @throws Exception if the method could not be activated
     */
    @ClusterActivateWorkUnit
    @SuppressWarnings("unchecked")
    public void activate()
            throws Exception
    {
        try {
            synchronized(sessionLock) {
                SLF4JLoggerProxy.info(this,
                                      "Activating {}",
                                      this);
                keyReader.execute(productKey,
                                  QuickFIXApplication.COMPONENT_NAME,
                                  Version.pomversion);
                isPrimary = true;
                int totalInstances = clusterData.getTotalInstances();
                int instanceId = clusterData.getInstanceNumber();
                List<FixSession> sessions = brokerService.findFixSessions(false,
                                                                          instanceId,
                                                                          totalInstances);
                Iterator<FixSession> sessionIterator = sessions.iterator();
                while(sessionIterator.hasNext()) {
                    FixSession session = sessionIterator.next();
                    if(!session.isEnabled()) {
                        SLF4JLoggerProxy.debug(this,
                                               "Discarding disabled session {}",
                                               session);
                        brokerService.reportBrokerStatusFromAll(session,
                                                                FixSessionStatus.DISABLED);
                        sessionIterator.remove();
                    }
                }
                FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
                // Initiate JMS.
                mListener = jmsManager.getIncomingJmsFactory().registerHandlerOEX(requestHandler,
                                                                                  Service.REQUEST_QUEUE,
                                                                                  false);
                qfApp = new QuickFIXApplication();
                qfApp.setSystemInfo(systemInfo);
                qfApp.setSupportedMessages(supportedMessages);
                qfApp.setPersister(replyPersister);
                qfApp.setUserManager(userManager);
                qfApp.setSender(quickFixSender);
                qfApp.setToClientStatus(jmsManager.getOutgoingJmsFactory().createJmsTemplateX(Service.BROKER_STATUS_TOPIC,
                                                                                              true));
                qfApp.setToTradeRecorder(null); // CD 20101202 - Removed as I don't think this is used any more and just consumes memory
                qfApp.setProductKey(productKey);
                qfApp.setAllowDeliverToCompID(allowDeliverToCompID);
                qfApp.addBrokerStatusListener(this);
                qfApp.addSessionStatusListener(this);
                qfApp.setClusterService(clusterService);
                qfApp.setReportService(reportService);
                qfApp.setForwardMessages(forwardMessages);
                qfApp.setDontForwardMessages(dontForwardMessages);
                qfApp.setRootOrderIdFactory(rootOrderIdFactory);
                qfApp.setBrokerService(brokerService);
                qfApp.setSessionService(sessionService);
                qfApp.setExecutionPoolDelay(executionPoolDelay);
                qfApp.setExecutionPoolTtl(executionPoolTtl);
                qfApp.setMaxExecutionPools(maxExecutionPools);
                qfApp.setReportDao(reportDao);
                qfApp.setFixSessionRestoreExecutor(fixSessionRestoreExecutor);
                qfApp.setFixInjectorDirectory(fixInjectorDirectory);
                qfApp.setFixSettingsProviderFactory(fixSettingsProviderFactory);
                qfApp.setOutgoingMessageService(outgoingMessageService);
                qfApp.setKeyReader(keyReader);
                qfApp.start();
                // Initiate broker connections.
                try {
                    jmxExporter = new JmxExporter();
                    jmxExporter.setRegistrationBehavior(JmxExporter.REGISTRATION_REPLACE_EXISTING);
                } catch (JMException e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
                synchronized(initiators) {
                    for(FixSession initiatorSession : sessions) {
                        SessionID initiatorSessionId = new SessionID(initiatorSession.getSessionId());
                        SessionSettings initiatorSessionSettings = brokerService.generateSessionSettings(Lists.newArrayList(initiatorSession));
                        ThreadedSocketInitiator initiator = new ThreadedSocketInitiator(qfApp,
                                                                                        fixSettingsProvider.getMessageStoreFactory(initiatorSessionSettings),
                                                                                        initiatorSessionSettings,
                                                                                        fixSettingsProvider.getLogFactory(initiatorSessionSettings),
                                                                                        fixSettingsProvider.getMessageFactory());
                        initiator.start();
                        // TODO try/catch?
                        jmxExporter.register(initiator);
                        initiators.put(initiatorSessionId,
                                       initiator);
                    }
                }
                // Initiate JMX (for application MBeans).
                MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
                mbeanServer.registerMBean(new ORSAdmin(quickFixSender,
                                                       idFactory,
                                                       userManager),
                                                       new ObjectName(JMX_NAME));
                activated = true;
            }
        } catch (Exception e) {
            EnterprisePlatformServices.handleException(this,
                                                       "Unable to activate DARE",
                                                       e);
            throw e;
        }
    }
    /**
     * Stops worker threads of the receiver.
     */
    @PreDestroy
    public void stop()
    {
        try {
            if(statusUpdater != null) {
                try {
                    statusUpdater.shutdownNow();
                } catch (Exception ignored) {}
                statusUpdater = null;
            }
            if(qfApp != null) {
                try {
                    qfApp.stop();
                } catch (Exception ignored) {}
                qfApp = null;
            }
            synchronized(initiators) {
                for(ThreadedSocketInitiator initiator : initiators.values()) {
                    for(Session session : initiator.getManagedSessions()) {
                        try {
                            session.disconnect("System shutdown",
                                               false);
                        } catch (Exception ignored) {}
                    }
                    try {
                        initiator.stop(true);
                    } catch (Exception ignored) {}
                }
                initiators.clear();
            }
            if (mListener!=null) {
                mListener.shutdown();
                mListener=null;
            }
        } finally {
            if(notificationExecutor != null) {
                notificationExecutor.notify(Notification.low("DARE Stopped",
                                                             "DARE Stopped at " + new DateTime(),
                                                             OrderRoutingSystem.class.getSimpleName()));
            }
            Messages.APP_STOP_SUCCESS.info(this);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionListener#sessionDisabled(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    public void sessionDisabled(FixSession inSession)
    {
        if(!inSession.isAcceptor()) {
            synchronized(sessionLock) {
                SessionID sessionId = new SessionID(inSession.getSessionId());
                Session activeSession = Session.lookupSession(sessionId);
                if(activeSession == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "No existing session on this instance for {}, nothing to do",
                                           inSession);
                } else {
                    SLF4JLoggerProxy.debug(this,
                                           "Disabling {} {}",
                                           inSession,
                                           activeSession);
                    synchronized(initiators) {
                        ThreadedSocketInitiator initiator = initiators.remove(sessionId);
                        if(initiator == null) {
                        } else {
                            initiator.stop(true);
                            initiator.removeDynamicSession(sessionId);
                        }
                    }
                }
                ClusteredBrokerStatus brokerStatus = brokerService.generateBrokerStatus(inSession,
                                                                                        clusterData,
                                                                                        FixSessionStatus.DISABLED,
                                                                                        false);
                brokerService.reportBrokerStatus(brokerStatus);
            }
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring disabled session {} because it is not an acceptor",
                                   inSession);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionListener#sessionEnabled(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    public void sessionEnabled(FixSession inSession)
    {
        if(inSession.isAcceptor()) {
            SLF4JLoggerProxy.debug(this,
                                   "Ignoring enabled acceptor session {}",
                                   inSession);
        } else {
            synchronized(sessionLock) {
                if(brokerService.isAffinityMatch(inSession,
                                                 clusterData)) {
                    SLF4JLoggerProxy.debug(this,
                                           "Enabling {}",
                                           inSession);
                    try {
                        SessionID newSessionId = new SessionID(inSession.getSessionId());
                        if(qfApp == null) {
                            SLF4JLoggerProxy.debug(this,
                                                   "Skipping dynamic add of initial session {}",
                                                   newSessionId);
                            return;
                        }
                        FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
                        SessionSettings sessionSettings = brokerService.generateSessionSettings(Lists.newArrayList(inSession));
                        SessionFactory sessionFactory = new DefaultSessionFactory(qfApp,
                                                                                  fixSettingsProvider.getMessageStoreFactory(sessionSettings),
                                                                                  fixSettingsProvider.getLogFactory(sessionSettings),
                                                                                  fixSettingsProvider.getMessageFactory());
                        Session newSession = sessionFactory.create(newSessionId,
                                                                   sessionSettings);
                        ThreadedSocketInitiator newInitiator = new ThreadedSocketInitiator(qfApp,
                                                                                           fixSettingsProvider.getMessageStoreFactory(sessionSettings),
                                                                                           sessionSettings,
                                                                                           fixSettingsProvider.getLogFactory(sessionSettings),
                                                                                           fixSettingsProvider.getMessageFactory());
                        synchronized(initiators) {
                            jmxExporter.register(newInitiator);
                            newInitiator.start();
                            initiators.put(newSessionId,
                                           newInitiator);
                        }
                        SLF4JLoggerProxy.debug(this,
                                               "Adding {} for {}",
                                               newSession,
                                               inSession);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to create new session for {}",
                                              inSession);
                        if(e instanceof RuntimeException) {
                            throw (RuntimeException)e;
                        }
                        throw new RuntimeException(e);
                    }
                } else {
                    SLF4JLoggerProxy.debug(this,
                                           "Ignoring enabled initiator session {} because of affinity mismatch",
                                           inSession);
                    // send status update
                    ClusteredBrokerStatus brokerStatus = brokerService.generateBrokerStatus(inSession,
                                                                                            clusterData,
                                                                                            FixSessionStatus.AFFINITY_MISMATCH,
                                                                                            false);
                    brokerService.reportBrokerStatus(brokerStatus);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionListener#sessionStopped(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    public void sessionStopped(FixSession inSession)
    {
        if(inSession.isAcceptor()) {
            SLF4JLoggerProxy.debug(this,
                                   "{} not stopping {} because the session is not an initiator session",
                                   this,
                                   inSession);
        } else {
            synchronized(sessionLock) {
                SessionID sessionId = new SessionID(inSession.getSessionId());
                Session activeSession = Session.lookupSession(sessionId);
                if(activeSession == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} not stopping {} because it is not currently active",
                                           this,
                                           inSession);
                } else {
                    SLF4JLoggerProxy.debug(this,
                                           "{} stopping {}:{}",
                                           this,
                                           inSession,
                                           activeSession);
                    try {
                        activeSession.disconnect("Stop invoked at " + new DateTime(),
                                                 false);
                        SLF4JLoggerProxy.debug(this,
                                               "{} {} stopped",
                                               this,
                                               inSession);
                    } catch (IOException e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "{} unable to stop {}",
                                              this,
                                              inSession);
                    }
                }
                ThreadedSocketInitiator initiator = initiators.get(sessionId);
                if(initiator == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} found no intiator to stop for {}, nothing to do",
                                           this,
                                           inSession);
                } else {
                    initiator.stop();
                    ClusteredBrokerStatus brokerStatus = brokerService.generateBrokerStatus(inSession,
                                                                                            clusterData,
                                                                                            FixSessionStatus.STOPPED,
                                                                                            false);
                    brokerService.reportBrokerStatus(brokerStatus);
                    SLF4JLoggerProxy.debug(this,
                                           "{} {} initiator stopped",
                                           this,
                                           inSession);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionListener#sessionStarted(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    public void sessionStarted(FixSession inSession)
    {
        if(inSession.isAcceptor()) {
            SLF4JLoggerProxy.debug(this,
                                   "{} not starting {} because the session is not an initiator session",
                                   this,
                                   inSession);
        } else {
            synchronized(sessionLock) {
                SessionID sessionId = new SessionID(inSession.getSessionId());
                ThreadedSocketInitiator initiator = initiators.get(sessionId);
                if(initiator == null) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} found no intiator to start for {}, nothing to do",
                                           this,
                                           inSession);
                } else {
                    try {
                        initiator.start();
                        SLF4JLoggerProxy.debug(this,
                                               "{} {} initiator started",
                                               this,
                                               inSession);
                    } catch (RuntimeError | ConfigError e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to start {}",
                                              inSession);
                    }
                }
            }
        }
    }
    /**
     * Indicate if this node has been activated.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isActive()
    {
        return activated;
    }
    /**
     * Get the supportedMessages value.
     *
     * @return a <code>MessageFilter</code> value
     */
    public MessageFilter getSupportedMessages()
    {
        return supportedMessages;
    }
    /**
     * Sets the supportedMessages value.
     *
     * @param inSupportedMessages a <code>MessageFilter</code> value
     */
    public void setSupportedMessages(MessageFilter inSupportedMessages)
    {
        supportedMessages = inSupportedMessages;
    }
    /**
     * Gets the <code>OrderRoutingSystem</code> value.
     *
     * @return an <code>OrderRoutingSystem</code> value
     */
    public static OrderRoutingSystem getInstance()
    {
        return instance;
    }
    /**
     * Get the systemInfoService value.
     *
     * @return a <code>SystemInfoService</code> value
     */
    public SystemInfoService getSystemInfoService()
    {
        return systemInfoService;
    }
    /**
     * Sets the systemInfoService value.
     *
     * @param inSystemInfoService a <code>SystemInfoService</code> value
     */
    public void setSystemInfoService(SystemInfoService inSystemInfoService)
    {
        systemInfoService = inSystemInfoService;
    }
    /**
     * Get the allowDeliverToCompID value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getAllowDeliverToCompID()
    {
        return allowDeliverToCompID;
    }
    /**
     * Sets the allowDeliverToCompID value.
     *
     * @param inAllowDeliverToCompID a <code>boolean</code> value
     */
    public void setAllowDeliverToCompID(boolean inAllowDeliverToCompID)
    {
        allowDeliverToCompID = inAllowDeliverToCompID;
    }
    /**
     * Get the clusterService value.
     *
     * @return a <code>ClusterService</code> value
     */
    public ClusterService getClusterService()
    {
        return clusterService;
    }
    /**
     * Sets the clusterService value.
     *
     * @param inClusterService a <code>ClusterService</code> value
     */
    public void setClusterService(ClusterService inClusterService)
    {
        clusterService = inClusterService;
    }
    /**
     * Get the fixInjectorDirectory value.
     *
     * @return a <code>String</code> value
     */
    public String getFixInjectorDirectory()
    {
        return fixInjectorDirectory;
    }
    /**
     * Sets the fixInjectorDirectory value.
     *
     * @param inFixInjectorDirectory a <code>String</code> value
     */
    public void setFixInjectorDirectory(String inFixInjectorDirectory)
    {
        fixInjectorDirectory = inFixInjectorDirectory;
    }
    /**
     * Get the reportService value.
     *
     * @return a <code>ReportService</code> value
     */
    public ReportService getReportService()
    {
        return reportService;
    }
    /**
     * Sets the reportService value.
     *
     * @param inReportService a <code>ReportService</code> value
     */
    public void setReportService(ReportService inReportService)
    {
        reportService = inReportService;
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * Get the sessionService value.
     *
     * @return a <code>SessionService</code> value
     */
    public SessionService getSessionService()
    {
        return sessionService;
    }
    /**
     * Sets the sessionService value.
     *
     * @param inSessionService a <code>SessionService</code> value
     */
    public void setSessionService(SessionService inSessionService)
    {
        sessionService = inSessionService;
    }
    /**
     * Get the dontForwardMessages value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getDontForwardMessages()
    {
        return dontForwardMessages;
    }
    /**
     * Sets the dontForwardMessages value.
     *
     * @param inDontForwardMessages a <code>String</code> value
     */
    public void setDontForwardMessages(String inDontForwardMessages)
    {
        dontForwardMessages.clear();
        if(inDontForwardMessages != null) {
            dontForwardMessages.addAll(Arrays.asList(inDontForwardMessages.split(",")));
        }
    }
    /**
     * Get the forwardMessages value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getForwardMessages()
    {
        return forwardMessages;
    }
    /**
     * Sets the forwardMessages value.
     *
     * @param inForwardMessages a <code>String</code> value
     */
    public void setForwardMessages(String inForwardMessages)
    {
        forwardMessages.clear();
        if(forwardMessages != null) {
            forwardMessages.addAll(Arrays.asList(inForwardMessages.split(",")));
        }
    }
    /**
     * Get the notificationExecutor value.
     *
     * @return a <code>NotificationExecutor</code> value
     */
    public NotificationExecutor getNotificationExecutor()
    {
        return notificationExecutor;
    }
    /**
     * Sets the notificationExecutor value.
     *
     * @param a <code>NotificationExecutor</code> value
     */
    public void setNotificationExecutor(NotificationExecutor inNotificationExecutor)
    {
        notificationExecutor = inNotificationExecutor;
    }
    /**
     * Reports backup status, if necessary, for the sessions for which this instance is serving as backup.
     */
    private void reportBackupStatus()
    {
        // deliberately select all sessions because we need to register as disabled if appropriate, even if the instance isn't an affinity match
        for(FixSession fixSession : brokerService.findFixSessions(false,
                                                                  1,
                                                                  1)) {
            if(fixSession.isAcceptor()) {
                // ignore acceptor sessions (shouldn't get any, anyway)
            } else {
                if(fixSession.isEnabled()) {
                    if(brokerService.isAffinityMatch(fixSession,
                                                     clusterData)) {
                        if(isPrimary) {
                            // report nothing, status will be reported elsewhere
                        } else {
                            // this is an enabled session which is an affinity match for this instance, but we not the primary in the cluster, report backup
                            SLF4JLoggerProxy.debug(this,
                                                   "{} reporting backup status for {}",
                                                   this,
                                                   fixSession);
                            ClusteredBrokerStatus brokerStatus = brokerService.generateBrokerStatus(fixSession,
                                                                                                    clusterData,
                                                                                                    FixSessionStatus.BACKUP,
                                                                                                    false);
                            brokerService.reportBrokerStatus(brokerStatus);
                        }
                    } else {
                        // this session is enabled, but not an affinity match for this instance, nothing to do
                    }
                } else {
                    // report disabled status
                    SLF4JLoggerProxy.debug(this,
                                           "{} reporting disabled status for {}",
                                           this,
                                           fixSession);
                    ClusteredBrokerStatus brokerStatus = brokerService.generateBrokerStatus(fixSession,
                                                                                            clusterData,
                                                                                            FixSessionStatus.DISABLED,
                                                                                            false);
                    brokerService.reportBrokerStatus(brokerStatus);
                }
            }
        }
    }
    /**
     * Verifies that the current database version matches the expected value.
     */
    private void verifyDatabaseVersion()
    {
        try {
            systemInfoService.verifyDatabaseVersion();
        } catch (DatabaseVersionMismatch e) {
            SLF4JLoggerProxy.error(this,
                                   "Database version {} does not match expected current version {}",
                                   e.getActualDatabaseVersion(),
                                   e.getExpectedDatabaseVersion());
            throw e;
        }
    }
    /**
     * DARE JMS identifier
     */
    private static final String JMX_NAME = "com.marketcetera.ors.mbean:type=ORSAdmin"; //$NON-NLS-1$
    /**
     * singleton instance reference
     */
    private static OrderRoutingSystem instance;
    /**
     * application responsible for managing FIX sessions
     */
    private QuickFIXApplication qfApp;
    /**
     * message listener container value
     */
    private SimpleMessageListenerContainer mListener;
    /**
     * sends messages to FIX destinations
     */
    @Autowired
    private QuickFIXSender quickFixSender;
    /**
     * creates unique identifiers
     */
    private IDFactory idFactory;
    /**
     * manages the JMS incoming connection
     */
    private JmsManager jmsManager;
    /**
     * provides access to report history services
     */
    private ReportHistoryServices reportHistoryServices;
    /**
     * persists replies received from brokers
     */
    private ReplyPersister replyPersister;
    /**
     * system info value
     */
    private SystemInfoImpl systemInfo;
    /**
     * manages users
     */
    private UserManager userManager;
    /**
     * creates client sessions
     */
    private ClientSessionFactory clientSessionFactory;
    /**
     * holds client sessions
     */
    private SessionManager<ClientSession> sessionManager;
    /**
     * web services server value
     */
    private Server<ClientSession> server;
    /**
     * provides web services
     */
    private Service service;
    /**
     * handles outgoing requests from client
     */
    private ReceiveOnlyHandler<DataEnvelope> requestHandler;
    /**
     * key data which allows access to ORS services
     */
    private String productKey;
    /**
     * provides key reader services
     */
    @Autowired
    private KeyReader keyReader;
    /**
     * provides access to system info objects
     */
    @Autowired
    private SystemInfoService systemInfoService;
    /**
     * determines whether we allow the redeliverToCompID flag or not
     */
    private boolean allowDeliverToCompID = false;
    /**
     * identifies this instance
     */
    private ClusterData clusterData;
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * uniquely identifies this work unit in the cluster - determined at runtime
     */
    private String clusterWorkUnitUid;
    /**
     * provides access to report services
     */
    @Autowired
    private ReportService reportService;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to session services
     */
    @Autowired
    private SessionService sessionService;
    /**
     * provides datastore access to persistent reports
     */
    @Autowired
    private PersistentReportDao reportDao;
    /**
     * restores FIX sessions on startup
     */
    @Autowired(required=false)
    @SuppressWarnings("rawtypes")
    private FixSessionRestoreExecutor fixSessionRestoreExecutor;
    /**
     * constructs a FIX settings provider object
     */
    @Autowired
    private FixSettingsProviderFactory fixSettingsProviderFactory;
    /**
     * directory name into which FIX injector files will be dropped, may be <code>null</code>
     */
    private String fixInjectorDirectory;
    /**
     * maximum number of order pools to have active at once
     */
    private int maxExecutionPools = 5;
    /**
     * interval to wait between checks for available order pools
     */
    private long executionPoolDelay = 10;
    /**
     * number of milliseconds to leave an order pool alive before retiring it
     */
    private long executionPoolTtl = 1000;
    /**
     * messages that should not be forwarded to clients (empty for all messages)
     */
    private final Set<String> dontForwardMessages = Sets.newHashSet("A","0","1","2","3","4","5");
    /**
     * messages that should be forwarded to client (empty for all messages)
     */
    private final Set<String> forwardMessages = new HashSet<>();
    /**
     * holds those interested in broker status updates
     */
    private final Deque<BrokerStatusListener> brokerStatusListeners = new ConcurrentLinkedDeque<>();
    /**
     * holds those interested in session status updates
     */
    private final Deque<SessionStatusListener> sessionStatusListeners = new ConcurrentLinkedDeque<>();
    /**
     * notification service, may be <code>null</code>
     */
    @Autowired(required=false)
    private NotificationExecutor notificationExecutor;
    /**
     * optional supported messages value
     */
    private MessageFilter supportedMessages;
    /**
     * JMX exporter value
     */
    private JmxExporter jmxExporter;
    /**
     * active initiators by session id
     */
    private final Map<SessionID,ThreadedSocketInitiator> initiators = new HashMap<>();
    /**
     * indicates if this host is the primary, active host
     */
    private boolean isPrimary = false;
    /**
     * indicates if this host has been successfully activated
     */
    private boolean activated = false;
    /**
     * creates or finds root order ids
     */
    @Autowired
    private RootOrderIdFactory rootOrderIdFactory;
    /**
     * provides access to outgoing message services
     */
    @Autowired
    private OutgoingMessageService outgoingMessageService;
    /**
     * task used to update status of sessions to backup, initially
     */
    private Runnable backupStatusTask;
    /**
     * used to update status
     */
    private ScheduledExecutorService statusUpdater;
    /**
     * guards access to session activities
     */
    private final Object sessionLock = new Object();
}
