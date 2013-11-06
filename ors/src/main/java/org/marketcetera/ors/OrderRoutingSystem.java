package org.marketcetera.ors;

import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.marketcetera.client.Service;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.core.IDFactory;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.brokers.Selector;
import org.marketcetera.ors.config.SpringConfig;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.info.SystemInfo;
import org.marketcetera.ors.info.SystemInfoImpl;
import org.marketcetera.ors.mbeans.ORSAdmin;
import org.marketcetera.ors.ws.ClientSession;
import org.marketcetera.ors.ws.ClientSessionFactory;
import org.marketcetera.ors.ws.ServiceImpl;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.util.auth.StandardAuthentication;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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

    // CLASS DATA.

    private static final Class<?> LOGGER_CATEGORY = OrderRoutingSystem.class;
//    private static final String APP_CONTEXT_CFG_BASE = "file:" + CONF_DIR + "properties.xml"; //$NON-NLS-1$ //$NON-NLS-2$
    private static final String JMX_NAME = "org.marketcetera.ors.mbean:type=ORSAdmin"; //$NON-NLS-1$
    /**
     * singleton instance reference
     */
    private static OrderRoutingSystem instance;

    // INSTANCE DATA.

//    private AbstractApplicationContext mContext;
//    private final StandardAuthentication mAuth;
    private Brokers mBrokers;
    private QuickFIXApplication mQFApp;
    private SimpleMessageListenerContainer mListener;
    private SocketInitiator mInitiator;
    private QuickFIXSender qSender;
    private IDFactory idFactory;
    private SpringConfig config;
    /**
     * provides access to user objects
     */
    @Autowired
    private UserService userService;
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
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        // Create system information.
        SystemInfoImpl systemInfo = new SystemInfoImpl();
        JmsManager jmsMgr = new JmsManager(config.getIncomingConnectionFactory(),
                                           config.getOutgoingConnectionFactory());
        ReportHistoryServices historyServices = config.getReportHistoryServices();
        systemInfo.setValue(SystemInfo.HISTORY_SERVICES,
                            historyServices);
        mBrokers = new Brokers(config.getBrokers(),
                               historyServices);
        Selector selector = new Selector(getBrokers(),
                                         config.getSelector());
        UserManager userManager = new UserManager();
        ReplyPersister persister = new ReplyPersister(historyServices,
                                                      config.getOrderInfoCache());
        historyServices.init(config.getIDFactory(),
                             jmsMgr,
                             persister);
        // Set dictionary for all QuickFIX/J messages we generate.
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));
        // Initiate web services.
        ClientSessionFactory clientSessionFactory = new ClientSessionFactory(systemInfo,
                                                                             jmsMgr,
                                                                             userManager);
        clientSessionFactory = new ClientSessionFactory(systemInfo,
                                                        jmsMgr,
                                                        userManager);
        clientSessionFactory.setUserService(userService);
        SessionManager<ClientSession> sessionManager = new SessionManager<ClientSession>(clientSessionFactory,
                                                                                         (config.getServerSessionLife()==SessionManager.INFINITE_SESSION_LIFESPAN) ? SessionManager.INFINITE_SESSION_LIFESPAN : (config.getServerSessionLife()*1000));
                                                                                         userManager.setSessionManager(sessionManager);
//        Authenticator authenticator = mContext.getBean(org.marketcetera.util.ws.stateful.Authenticator.class);
        Server<ClientSession> server = new Server<ClientSession>(config.getServerHost(),
                                                                 config.getServerPort(),
                                                                 null, //authenticator,
                                                                 sessionManager);
        ServiceImpl service = new ServiceImpl(sessionManager,
                                              getBrokers(),
                                              config.getIDFactory(),
                                              historyServices,
                                              config.getSymbolResolverServices(),
                                              userService);
        server.publish(service,
                       Service.class);
        // Initiate JMS.
        qSender = new QuickFIXSender();
        LocalIDFactory localIdFactory = new LocalIDFactory(config.getIDFactory());
        localIdFactory.init();
        RequestHandler handler = new RequestHandler(getBrokers(),
                                                    selector,
                                                    config.getAllowedOrders(),
                                                    persister,
                                                    qSender,
                                                    userManager,
                                                    localIdFactory);
//        mListener = jmsMgr.getIncomingJmsFactory().registerHandlerOEX(handler,
//                                                                      Service.REQUEST_QUEUE,
//                                                                      false);
        mQFApp = new QuickFIXApplication(systemInfo,getBrokers(),
                                         config.getSupportedMessages(),
                                         persister,
                                         qSender,
                                         userManager,
                                         jmsMgr.getOutgoingJmsFactory().createJmsTemplateX(Service.BROKER_STATUS_TOPIC,
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
                                               qSender,
                                               localIdFactory,
                                               userManager),
                                  new ObjectName(JMX_NAME));
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
     * Prints the given message alongside usage information on the
     * standard error stream, and throws an exception.
     *
     * @param message The message.
     *
     * @throws IllegalStateException Always thrown.
     */

    private void printUsage
        (I18NBoundMessage message)
        throws I18NException
    {
        System.err.println(message.getText());
        System.err.println(Messages.APP_USAGE.getText
                           (OrderRoutingSystem.class.getName()));
        System.err.println(Messages.APP_AUTH_OPTIONS.getText());
        System.err.println();
        getAuth().printUsage(System.err);
        throw new I18NException(message);
    }

    /**
     * Stops worker threads of the receiver.
     *
     * @return The context.
     */
    synchronized void stop()
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
                    qSender.sendToTarget(logout,
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
//        if (mContext!=null) {
//            mContext.close();
//            mContext=null;
//        }
    }

    /**
     * Returns the receiver's authentication system.
     *
     * @return The authentication system.
     */

    StandardAuthentication getAuth()
    {
        throw new UnsupportedOperationException(); // TODO
//        return mAuth;
    }

    /**
     * Returns the receiver's brokers.
     *
     * @return The brokers.
     */

    Brokers getBrokers()
    {
        return mBrokers;
    }
    // CLASS METHODS.
    /**
     * Gets the <code>OrderRoutingSystem</code> value.
     *
     * @return an <code>OrderRoutingSystem</code> value
     */
    public static OrderRoutingSystem getInstance()
    {
        return instance;
    }
}
