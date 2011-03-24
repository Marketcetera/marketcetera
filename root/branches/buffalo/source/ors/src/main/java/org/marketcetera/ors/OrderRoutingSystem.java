package org.marketcetera.ors;

import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.brokers.Selector;
import org.marketcetera.ors.config.SpringConfig;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.info.SystemInfo;
import org.marketcetera.ors.info.SystemInfoImpl;
import org.marketcetera.ors.mbeans.ORSAdmin;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.util.auth.StandardAuthentication;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.AbstractApplicationContext;
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
        implements Lifecycle, InitializingBean
{
    /**
     * Returns the server's broker status.
     *
     * @param context The context.
     *
     * @return The status
     */
    public BrokersStatus getBrokersStatus()
    {
        return getBrokers().getStatus();
    }
    
    // INSTANCE METHODS.
    /**
     * Prints the given message alongside usage information on the
     * standard error stream, and throws an exception.
     *
     * @param message The message.
     *
     * @throws IllegalStateException Always thrown.
     */
    private void printUsage(I18NBoundMessage message)
    {
        System.err.println(message.getText());
        System.err.println(Messages.APP_USAGE.getText
                           (OrderRoutingSystem.class.getName()));
        System.err.println(Messages.APP_AUTH_OPTIONS.getText());
        System.err.println();
        getAuth().printUsage(System.err);
        throw new IllegalStateException(message.getText());
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        if(config == null) {
            throw new IllegalStateException(Messages.APP_NO_CONFIGURATION.getText());
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        // TODO Auto-generated method stub
        return true;
    }
    /**
     * Get the config value.
     *
     * @return a <code>SpringConfig</code> value
     */
    SpringConfig getConfig()
    {
        return config;
    }
    /**
     * Sets the config value.
     *
     * @param a <code>SpringConfig</code> value
     */
    public void setConfig(SpringConfig inConfig)
    {
        config = inConfig;
    }
    /**
     * Get the propertiesPath value.
     *
     * @return a <code>String</code> value
     */
    String getConfigPath()
    {
        return configPath;
    }
    /**
     * Sets the propertiesPath value.
     *
     * @param a <code>String</code> value
     */
    public void setConfigPath(String inConfigPath)
    {
        configPath = inConfigPath;
    }
    /**
     * 
     */
    private volatile String configPath;
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        try {
            // I think this whole chunk needs to go
            // TODO start
//            // Obtain authorization credentials.
//            String[] args = new String[0]; // CD these were the commandline args used by the CLI
//            mAuth = new StandardAuthentication(propertiesFile.getAbsolutePath(),
//                                               args);
//            if(!getAuth().setValues()) {
//                printUsage(Messages.APP_MISSING_CREDENTIALS);
//            }
//            args=getAuth().getOtherArgs();
//            if (args.length!=0) {
//                printUsage(Messages.APP_NO_ARGS_ALLOWED);
//            }
            // TODO complete
//          StaticApplicationContext parentContext = new StaticApplicationContext(new FileSystemXmlApplicationContext(getConfigPath()));
            // TODO remove this, too
//            SpringUtils.addStringBean(parentContext,
//                                      ApplicationBase.USERNAME_BEAN_NAME,
//                                      getAuth().getUser());
//            SpringUtils.addStringBean(parentContext,
//                                      ApplicationBase.PASSWORD_BEAN_NAME,
//                                      getAuth().getPasswordAsString());
//            parentContext.refresh();
            // TODO done remove
            // Read Spring configuration.
//            mContext = new FileSystemXmlApplicationContext(new String[] {"file:" + mainFile.getAbsolutePath()}, //$NON-NLS-1$
//                                                           parentContext);
//            mContext.start();
            // Create system information.
            SystemInfoImpl systemInfo = new SystemInfoImpl();
            // Create resource managers.
            config.getIDFactory().init();
//            JmsManager jmsMgr = new JmsManager(config.getIncomingConnectionFactory(),
//                                               config.getOutgoingConnectionFactory());
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
                                 null,
                                 persister);
            // Set dictionary for all QuickFIX/J messages we generate.
            CurrentFIXDataDictionary.setCurrentFIXDataDictionary(FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));
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
//            mListener = jmsMgr.getIncomingJmsFactory().registerHandlerOEX(handler,
//                                                                          Service.REQUEST_QUEUE,
//                                                                          false);
//            mListener.start();
            mQFApp = new QuickFIXApplication(systemInfo,getBrokers(),
                                             config.getSupportedMessages(),
                                             persister,
                                             qSender,
                                             userManager,
                                             null,
                                             null);
//                                             jmsMgr.getOutgoingJmsFactory().createJmsTemplateX(Service.BROKER_STATUS_TOPIC,
//                                                                                               true),
//                                             jmsMgr.getOutgoingJmsFactory().createJmsTemplateQ(TRADE_RECORDER_QUEUE,
//                                                                                               false));
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Stops worker threads of the receiver.
     */
    @Override
    public synchronized void stop()
    {
        Brokers brokers = getBrokers();
        if(brokers != null) {
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
    // CLASS DATA.
    private static final String TRADE_RECORDER_QUEUE = "trade-recorder"; //$NON-NLS-1$
    private static final String JMX_NAME = "org.marketcetera.ors.mbean:type=ORSAdmin"; //$NON-NLS-1$
    // INSTANCE DATA.
    private AbstractApplicationContext mContext;
    private volatile StandardAuthentication mAuth;
    private volatile Brokers mBrokers;
    private volatile QuickFIXApplication mQFApp;
    private SimpleMessageListenerContainer mListener;
    private SocketInitiator mInitiator;
    private volatile QuickFIXSender qSender;
    /**
     * 
     */
    private volatile SpringConfig config;
}
