package org.marketcetera.ors;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.client.Service;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.ApplicationVersion;
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
import org.marketcetera.ors.ws.DBAuthenticator;
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
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import quickfix.DefaultMessageFactory;
import quickfix.Message;
import quickfix.SessionNotFound;
import quickfix.SocketInitiator;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.TargetCompID;

/**
 * The main application. See {@link SpringConfig} for configuration
 * information.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class OrderRoutingSystem
    extends ApplicationBase
{

    // CLASS DATA.

    private static final Class<?> LOGGER_CATEGORY = OrderRoutingSystem.class;
    private static final String APP_CONTEXT_CFG_BASE = "file:" + CONF_DIR + "properties.xml"; //$NON-NLS-1$ //$NON-NLS-2$
    private static final String JMX_NAME = "org.marketcetera.ors.mbean:type=ORSAdmin"; //$NON-NLS-1$
    /**
     * singleton instance reference
     */
    private static OrderRoutingSystem instance;

    // INSTANCE DATA.

    private AbstractApplicationContext mContext;
    private final StandardAuthentication mAuth;
    private final Brokers mBrokers;
    private final QuickFIXApplication mQFApp;
    private SimpleMessageListenerContainer mListener;
    private SocketInitiator mInitiator;
    private final QuickFIXSender qSender;

    // CONSTRUCTORS.

    /**
     * Creates a new application given the command-line arguments. The
     * application spawns child threads, but the constructor does not
     * block while waiting for those threads to terminate; instead, it
     * returns as soon as construction is complete.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception Thrown if construction fails.
     */

    public OrderRoutingSystem(String[] args)
        throws Exception
    {
        // Obtain authorization credentials.
        instance = this;
        mAuth=new StandardAuthentication(APP_CONTEXT_CFG_BASE,args);
        if (!getAuth().setValues()) {
            printUsage(Messages.APP_MISSING_CREDENTIALS);
        }
        args=getAuth().getOtherArgs();
        if (args.length!=0) {
            printUsage(Messages.APP_NO_ARGS_ALLOWED);
        }
        StaticApplicationContext parentContext=
            new StaticApplicationContext
            (new FileSystemXmlApplicationContext(APP_CONTEXT_CFG_BASE));
        SpringUtils.addStringBean
            (parentContext,USERNAME_BEAN_NAME,
             getAuth().getUser());
        SpringUtils.addStringBean
            (parentContext,PASSWORD_BEAN_NAME,
             getAuth().getPasswordAsString());
        parentContext.refresh();

        // Read Spring configuration.

        mContext=new FileSystemXmlApplicationContext
            (new String[] {"file:"+CONF_DIR+ //$NON-NLS-1$
                           "server.xml"}, //$NON-NLS-1$
                parentContext);
        mContext.start();

        // Create system information.

        SystemInfoImpl systemInfo=new SystemInfoImpl();

        // Create resource managers.

        SpringConfig cfg=SpringConfig.getSingleton();
        if (cfg==null) {
            throw new I18NException(Messages.APP_NO_CONFIGURATION);
        }
        cfg.getIDFactory().init();
        JmsManager jmsMgr=new JmsManager
            (cfg.getIncomingConnectionFactory(),
             cfg.getOutgoingConnectionFactory());
        ReportHistoryServices historyServices=cfg.getReportHistoryServices();
        systemInfo.setValue
            (SystemInfo.HISTORY_SERVICES,historyServices);
        mBrokers=new Brokers(cfg.getBrokers(),historyServices);
        Selector selector=new Selector(getBrokers(),cfg.getSelector());
        UserManager userManager=new UserManager();
        ReplyPersister persister=new ReplyPersister
            (historyServices,cfg.getOrderInfoCache());
        historyServices.init(cfg.getIDFactory(),jmsMgr,persister);

        // Set dictionary for all QuickFIX/J messages we generate.

        CurrentFIXDataDictionary.setCurrentFIXDataDictionary
            (FIXDataDictionary.initializeDataDictionary
             (FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));

        // Initiate web services.

        SessionManager<ClientSession> sessionManager=
            new SessionManager<ClientSession>
            (new ClientSessionFactory(systemInfo,jmsMgr,userManager),
             (cfg.getServerSessionLife()==
              SessionManager.INFINITE_SESSION_LIFESPAN)?
             SessionManager.INFINITE_SESSION_LIFESPAN:
             (cfg.getServerSessionLife()*1000));
        userManager.setSessionManager(sessionManager);
        Server<ClientSession> server=new Server<ClientSession>
            (cfg.getServerHost(),cfg.getServerPort(),
             new DBAuthenticator(),sessionManager);
        server.publish
            (new ServiceImpl(sessionManager,getBrokers(),
                             cfg.getIDFactory(),historyServices),
             Service.class);

        // Initiate JMS.

        qSender=new QuickFIXSender();
        LocalIDFactory localIdFactory=new LocalIDFactory(cfg.getIDFactory());
        localIdFactory.init();
        RequestHandler handler=new RequestHandler
            (getBrokers(),selector,cfg.getAllowedOrders(),
             persister,qSender,userManager,localIdFactory);
        mListener=jmsMgr.getIncomingJmsFactory().registerHandlerOEX
            (handler,Service.REQUEST_QUEUE,false);
        mQFApp=new QuickFIXApplication(systemInfo,getBrokers(),cfg.getSupportedMessages(),
                                       persister,
                                       qSender,
                                       userManager,
                                       jmsMgr.getOutgoingJmsFactory().createJmsTemplateX(Service.BROKER_STATUS_TOPIC,
                                                                                         true),
                                       null); // CD 20101202 - Removed as I don't think this is used any more and just consumes memory

        // Initiate broker connections.
        SpringSessionSettings settings=getBrokers().getSettings();
        mInitiator = new SocketInitiator(mQFApp,
                                         settings.getQMessageStoreFactory(),
                                         settings.getQSettings(),
                                         settings.getQLogFactory(),
                                         new DefaultMessageFactory());
        mInitiator.start();
        // Initiate JMX (for QuickFIX/J and application MBeans).

        MBeanServer mbeanServer=ManagementFactory.getPlatformMBeanServer();
        (new JmxExporter(mbeanServer)).export(mInitiator);
        mbeanServer.registerMBean(new ORSAdmin(getBrokers(),
                                               qSender,
                                               localIdFactory,
                                               userManager),
                                  new ObjectName(JMX_NAME));
    }

    // INSTANCE METHODS.

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
                logout.getHeader().setField(new SenderCompID(broker.getSpringBroker().getDescriptor().getDictionary().get("SenderCompID")));
                logout.getHeader().setField(new TargetCompID(broker.getSpringBroker().getDescriptor().getDictionary().get("TargetCompID")));
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
        if (mContext!=null) {
            mContext.close();
            mContext=null;
        }
    }

    /**
     * Returns the receiver's authentication system.
     *
     * @return The authentication system.
     */

    StandardAuthentication getAuth()
    {
        return mAuth;
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
    /**
     * Main program.
     *
     * @param args The command-line arguments.
     */

    public static void main
        (String[] args)
    {
        // Configure logger.

        PropertyConfigurator.configureAndWatch
            (ApplicationBase.CONF_DIR+"log4j"+ //$NON-NLS-1$
             File.separator+"server.properties", //$NON-NLS-1$
             LOGGER_WATCH_DELAY);

        // Log application start.

        Messages.APP_COPYRIGHT.info(LOGGER_CATEGORY);
        Messages.APP_VERSION_BUILD.info(LOGGER_CATEGORY,
                ApplicationVersion.getVersion(),
                ApplicationVersion.getBuildNumber());
        Messages.APP_START.info(LOGGER_CATEGORY);

        // Start ORS.

        final OrderRoutingSystem ors;
        try {
            ors=new OrderRoutingSystem(args);
        } catch (Throwable t) {
            try {
                Messages.APP_STOP_ERROR.error(LOGGER_CATEGORY,t);
            } catch (Throwable t2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                t2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                t.printStackTrace();
            }
            return;
        }
        Messages.APP_STARTED.info(LOGGER_CATEGORY);

        // Hook to log shutdown.

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ors.stop();
                Messages.APP_STOP.info(LOGGER_CATEGORY);
            }
        });

        // Execute application.

        try {
            ors.startWaitingForever();
        } catch (Throwable t) {
            try {
                Messages.APP_STOP_ERROR.error(LOGGER_CATEGORY,t);
            } catch (Throwable t2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                t2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                t.printStackTrace();
            }
            return;
        }
        Messages.APP_STOP_SUCCESS.info(LOGGER_CATEGORY);
    }
}
