package org.marketcetera.ors;

import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.Service;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.core.ComparableTask;
import org.marketcetera.core.IDFactory;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.brokers.Selector;
import org.marketcetera.ors.config.SpringConfig;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.info.SystemInfo;
import org.marketcetera.ors.info.SystemInfoImpl;
import org.marketcetera.ors.mbeans.ORSAdmin;
import org.marketcetera.ors.ws.ClientSession;
import org.marketcetera.ors.ws.ClientSessionFactory;
import org.marketcetera.quickfix.*;
import org.marketcetera.util.auth.StandardAuthentication;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import quickfix.DefaultMessageFactory;
import quickfix.Message;
import quickfix.SessionNotFound;
import quickfix.SocketInitiator;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.TargetCompID;

/* $License$ */

/**
 * Routes orders to order destination and maintains FIX sessions.
 *
 * @author tlerios@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderRoutingSystem
        implements InitializingBean
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
     * Get the config value.
     *
     * @return a <code>SpringConfig</code> value
     */
    public SpringConfig getConfig()
    {
        return config;
    }
    /**
     * Sets the config value.
     *
     * @param inConfig a <code>SpringConfig</code> value
     */
    public void setConfig(SpringConfig inConfig)
    {
        config = inConfig;
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
     * Get the selector value.
     *
     * @return a <code>Selector</code> value
     */
    public Selector getSelector()
    {
        return selector;
    }
    /**
     * Sets the selector value.
     *
     * @param inSelector a <code>Selector</code> value
     */
    public void setSelector(Selector inSelector)
    {
        selector = inSelector;
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
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(replyPersister,
                         "ORS reply persister required"); // TODO
        Validate.notNull(reportHistoryServices,
                         "ORS report history services required"); // TODO
        Validate.notNull(jmsManager,
                         "ORS JMS manager required"); // TODO
        Validate.notNull(systemInfo,
                         "ORS System Info required"); // TODO
        Validate.notNull(brokers,
                         "ORS brokers required"); // TODO
        Validate.notNull(selector,
                         "ORS selector required"); // TODO
        Validate.notNull(userManager,
                         "ORS user manager required"); // TODO
        Validate.notNull(clientSessionFactory,
                         "ORS client session factory required"); // TODO
        Validate.notNull(sessionManager,
                         "ORS session manager required"); // TODO
        Validate.notNull(server,
                         "ORS server required"); // TODO
        Validate.notNull(service,
                         "ORS service required"); // TODO
        Validate.notNull(quickFixSender,
                         "ORS quick fix server required"); // TODO
        // Create system information.
        systemInfo.setValue(SystemInfo.HISTORY_SERVICES,
                            reportHistoryServices);
        reportHistoryServices.init(config.getIDFactory(),
                                   jmsManager,
                                   replyPersister);
        // Set dictionary for all QuickFIX/J messages we generate.
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));
        // Initiate web services
        userManager.setSessionManager(sessionManager);
        server.publish(service,
                       Service.class);
        // Initiate JMS.
        LocalIDFactory localIdFactory = new LocalIDFactory(config.getIDFactory());
        localIdFactory.init();
        RequestHandler handler = new RequestHandler(getBrokers(),
                                                    selector,
                                                    config.getAllowedOrders(),
                                                    replyPersister,
                                                    quickFixSender,
                                                    userManager,
                                                    localIdFactory);
        mListener = jmsManager.getIncomingJmsFactory().registerHandlerOEX(handler,
                                                                          Service.REQUEST_QUEUE,
                                                                          false);
        mQFApp = new QuickFIXApplication(systemInfo,getBrokers(),
                                         config.getSupportedMessages(),
                                         replyPersister,
                                         quickFixSender,
                                         userManager,
                                         jmsManager.getOutgoingJmsFactory().createJmsTemplateX(Service.BROKER_STATUS_TOPIC,
                                                                                               true),
                                         null); // CD 20101202 - Removed as I don't think this is used any more and just consumes memory
        // Initiate broker connections.
        SpringSessionSettings settings = getBrokers().getSettings();
        mInitiator = new SocketInitiator(mQFApp,
                                         settings.getQMessageStoreFactory(),
                                         settings.getQSettings(),
                                         settings.getQLogFactory(),
                                         new DefaultMessageFactory());
        mInitiator.start();
        // Initiate JMX (for QuickFIX/J and application MBeans).
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        (new JmxExporter(mbeanServer)).export(mInitiator);
        mbeanServer.registerMBean(new ORSAdmin(getBrokers(),
                                               quickFixSender,
                                               localIdFactory,
                                               userManager),
                                  new ObjectName(JMX_NAME));
        ComparableTask orsStopTask = new ComparableTask() {
            @Override
            public void run()
            {
                stop();
            }
            @Override
            public int compareTo(ComparableTask inO)
            {
                return 0;
            }
        };
        ApplicationContainer.addShutdownTask(orsStopTask);
    }
    /**
     * Gets the <code>OrderReceiver</code> value.
     *
     * @return an <code>OrderReceiver</code> value
     */
    public ReportReceiver getOrderReceiver()
    {
        return mQFApp;
    }
    /**
     * Stops worker threads of the receiver.
     */
    public synchronized void stop()
    {
        Brokers brokers = getBrokers();
        for(Broker broker : brokers.getBrokers()) {
            if(broker.getSpringBroker().getFixLogoutRequired()) {
                SLF4JLoggerProxy.debug(OrderRoutingSystem.class,
                                       "Broker {} requires FIX logout", //$NON-NLS-1$
                                       broker.getBrokerID());
                Message logout = broker.getFIXMessageFactory().createMessage(MsgType.LOGOUT);
                // set mandatory fields
                logout.getHeader().setField(new SenderCompID(broker.getSpringBroker().getDescriptor().getDictionary().get("SenderCompID"))); //$NON-NLS-1$
                logout.getHeader().setField(new TargetCompID(broker.getSpringBroker().getDescriptor().getDictionary().get("TargetCompID"))); //$NON-NLS-1$
                logout.getHeader().setField(new SendingTime(new Date()));
                logout.toString();
                try {
                    SLF4JLoggerProxy.debug(OrderRoutingSystem.class,
                                           "Sending logout message {} to broker {}", //$NON-NLS-1$
                                           logout,
                                           broker.getBrokerID());
                    quickFixSender.sendToTarget(logout,
                                         broker.getSessionID());
                } catch (SessionNotFound e) {
                    SLF4JLoggerProxy.warn(OrderRoutingSystem.class,
                                          e,
                                          "Unable to logout from {}", // TODO
                                          broker.getBrokerID());
                }
            } else {
                SLF4JLoggerProxy.debug(OrderRoutingSystem.class,
                                       "Broker {} does not require FIX logout", //$NON-NLS-1$
                                       broker.getBrokerID());
            }
        }
        if (mInitiator!=null) {
            mInitiator.stop();
            mInitiator=null;
        }
        if (mListener!=null) {
            mListener.shutdown();
            mListener=null;
        }
    }
    /**
     * Returns the receiver's authentication system.
     *
     * @return The authentication system.
     */
    StandardAuthentication getAuth()
    {
        return ApplicationContainer.getInstance().getAuthentication();
    }
    /**
     * Returns the receiver's brokers.
     *
     * @return The brokers.
     */
    public Brokers getBrokers()
    {
        return brokers;
    }
    /**
     * Sets the brokers value.
     *
     * @param inBrokers a <code>Brokers</code> value
     */
    public void setBrokers(Brokers inBrokers)
    {
        brokers = inBrokers;
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
    private static final String JMX_NAME = "org.marketcetera.ors.mbean:type=ORSAdmin"; //$NON-NLS-1$
    /**
     * singleton instance reference
     */
    private static OrderRoutingSystem instance;
    /**
     * 
     */
    private Brokers brokers;
    private QuickFIXApplication mQFApp;
    private SimpleMessageListenerContainer mListener;
    private SocketInitiator mInitiator;
    /**
     * 
     */
    private QuickFIXSender quickFixSender;
    private IDFactory idFactory;
    private SpringConfig config;
    /**
     * 
     */
    private JmsManager jmsManager;
    /**
     * 
     */
    private ReportHistoryServices reportHistoryServices;
    /**
     * 
     */
    private ReplyPersister replyPersister;
    /**
     * 
     */
    private SystemInfoImpl systemInfo;
    /**
     * 
     */
    private Selector selector;
    /**
     * 
     */
    private UserManager userManager;
    /**
     * 
     */
    private ClientSessionFactory clientSessionFactory;
    /**
     * 
     */
    private SessionManager<ClientSession> sessionManager;
    /**
     * 
     */
    private Server<ClientSession> server;
    /**
     * 
     */
    private Service service;
}
