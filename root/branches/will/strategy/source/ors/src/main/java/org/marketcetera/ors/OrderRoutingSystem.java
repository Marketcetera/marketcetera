package org.marketcetera.ors;

import org.marketcetera.util.auth.StandardAuthentication;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.spring.SpringUtils;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import org.marketcetera.core.*;
import org.marketcetera.ors.mbeans.ORSAdmin;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.context.ApplicationContext;
import quickfix.SocketInitiator;

import org.apache.log4j.PropertyConfigurator;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * OrderRoutingSystem
 * Main entrypoint for sending orders and receiving responses from a FIX engine
 *
 * The ORS is configured using Spring, using the following modules:
 * <ol>
 *   <li>{@link OutgoingMessageHandler} which handles running the received
 *      order through modifiers, sending it on and generating and returning an
 *      immediate execution report </li>
 *   <li>{@link QuickFIXApplication} - a wrapper for setting up a FIX application (listener/sender)</li>
 *   <li>{@link org.marketcetera.quickfix.QuickFIXSender} = actually sends the FIX messages</li>
 * </ol>
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderRoutingSystem extends ApplicationBase {

    public static final String CFG_BASE_FILE_NAME=
        "file:"+CONF_DIR+"ors_base.xml"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final String LOGGER_NAME = OrderRoutingSystem.class.getName();
    public static final String[] APP_CONTEXT_CONFIG_FILES =
            {"quickfixj.xml", "message-modifiers.xml", "order-limits.xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    "ors.xml", "ors-shared.xml", "ors_db.xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    "ors_orm_vendor.xml", "ors_orm.xml"}; //$NON-NLS-1$ //$NON-NLS-2$

    private static StandardAuthentication authentication;
    private volatile static OrderRoutingSystem instance = null;

    public static void initializeLogger(String logConfig)
    {
        PropertyConfigurator.configureAndWatch
            (ApplicationBase.CONF_DIR+logConfig, LOGGER_WATCH_DELAY);
    }

    private static void usage()
    {
        System.err.println(Messages.APP_USAGE.getText(OrderRoutingSystem.class.getName()));
        System.err.println(Messages.APP_AUTH_OPTIONS.getText());
        System.err.println();
        authentication.printUsage(System.err);
        System.exit(1);
    }

    public static void main(String [] args) throws ConfigFileLoadingException
    {
        try {
            initializeLogger(LOGGER_CONF_FILE);

            authentication=new StandardAuthentication(CFG_BASE_FILE_NAME,args);
            if (!authentication.setValues()) {
                usage();
            }
            args=authentication.getOtherArgs();
            if (args.length!=0) {
                System.err.println(Messages.APP_NO_ARGS_ALLOWED.getText());
                usage();
            }
            StaticApplicationContext parentContext=
                new StaticApplicationContext
                (new FileSystemXmlApplicationContext(CFG_BASE_FILE_NAME));
            SpringUtils.addStringBean
                (parentContext,USERNAME_BEAN_NAME,
                 authentication.getUser());
            SpringUtils.addStringBean
                (parentContext,PASSWORD_BEAN_NAME,
                 authentication.getPasswordAsString());
            parentContext.refresh();

            instance = new OrderRoutingSystem();
            ApplicationContext appCtx = instance.createApplicationContext(APP_CONTEXT_CONFIG_FILES, parentContext, true);

            String connectHost = (String) appCtx.getBean("socketConnectHostValue", String.class); //$NON-NLS-1$
            String connectPort = (String) appCtx.getBean("socketConnectPortValue", String.class); //$NON-NLS-1$
            Messages.CONNECTING_TO.info(LOGGER_NAME, connectHost, connectPort);

            SocketInitiator initiator = (SocketInitiator) appCtx.getBean("socketInitiator", SocketInitiator.class); //$NON-NLS-1$
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            JmxExporter exporter = new JmxExporter(mbeanServer);
            exporter.export(initiator);
            ORSAdmin orsAdmin = (ORSAdmin) appCtx.getBean("orsAdmin", ORSAdmin.class); //$NON-NLS-1$
            mbeanServer.registerMBean(orsAdmin, new ObjectName("org.marketcetera.ors.mbean:type=ORSAdmin")); //$NON-NLS-1$

            instance.startWaitingForever();
            SLF4JLoggerProxy.debug(LOGGER_NAME, "ORS main finishing"); //$NON-NLS-1$
        } catch (Exception ex) {
            Messages.ERROR_STACK_TRACE.error(LOGGER_NAME, ex);
            Messages.ERROR_CONFIG.error(LOGGER_NAME, ex);
        } finally {
            Messages.APP_EXIT.info(LOGGER_NAME);
        }
    }

    /**
     * Returns the application instance.
     * @return the ORS instance. Null, if the application is not completely configured.
     */
    static OrderRoutingSystem getInstance() {
        return instance;
    }

}

