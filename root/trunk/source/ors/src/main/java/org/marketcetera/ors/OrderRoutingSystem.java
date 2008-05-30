package org.marketcetera.ors;

import org.marketcetera.util.auth.StandardAuthentication;
import org.marketcetera.util.spring.SpringUtils;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import org.marketcetera.core.*;
import org.marketcetera.ors.mbeans.ORSAdmin;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.context.ApplicationContext;
import quickfix.SocketInitiator;

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
@ClassVersion("$Id$")
public class OrderRoutingSystem extends ApplicationBase {

    private static final String CFG_BASE_FILE_NAME=
        "file:"+CONF_DIR+"ors_base.xml";

    private static final String LOGGER_NAME = OrderRoutingSystem.class.getName();
    public static final MessageBundleInfo ORS_MESSAGE_BUNDLE_INFO = new MessageBundleInfo("ors", "ors_messages");
    public static final String[] APP_CONTEXT_CONFIG_FILES =
    {"quickfixj.xml", "message-modifiers.xml", "order-limits.xml",
     "ors.xml", "ors-shared.xml"};

    private static StandardAuthentication authentication;

    protected List<MessageBundleInfo> getLocalMessageBundles() {
        LinkedList<MessageBundleInfo> bundles = new LinkedList<MessageBundleInfo>();
        bundles.add(ORS_MESSAGE_BUNDLE_INFO);
        return bundles;
    }

    private static void usage()
    {
        System.err.println("Usage: java "+
                           OrderRoutingSystem.class.getName());
        System.err.println("Authentication options:");
        System.err.println();
        authentication.printUsage(System.err);
        System.exit(1);
    }

    public static void main(String [] args) throws ConfigFileLoadingException
    {
        try {
            authentication=new StandardAuthentication(CFG_BASE_FILE_NAME,args);
            if (!authentication.setValues()) {
                usage();
            }
            args=authentication.getOtherArgs();
            if (args.length!=0) {
                System.err.println("No arguments are allowed");
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

            OrderRoutingSystem ors = new OrderRoutingSystem();
            ApplicationContext appCtx = ors.createApplicationContext(APP_CONTEXT_CONFIG_FILES, parentContext, true);

            if(LoggerAdapter.isInfoEnabled(LOGGER_NAME)) {
                String connectHost = (String) appCtx.getBean("socketConnectHostValue", String.class);
                String connectPort = (String) appCtx.getBean("socketConnectPortValue", String.class);
                LoggerAdapter.info(ORSMessageKey.CONNECTING_TO.getLocalizedMessage(connectHost, connectPort), LOGGER_NAME);
            }

            SocketInitiator initiator = (SocketInitiator) appCtx.getBean("socketInitiator", SocketInitiator.class);
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            JmxExporter exporter = new JmxExporter(mbeanServer);
            exporter.export(initiator);
            ORSAdmin orsAdmin = (ORSAdmin) appCtx.getBean("orsAdmin", ORSAdmin.class);
            mbeanServer.registerMBean(orsAdmin, new ObjectName("org.marketcetera.ors.mbean:type=ORSAdmin"));

            ors.startWaitingForever();
            if(LoggerAdapter.isDebugEnabled(LOGGER_NAME)) { LoggerAdapter.debug("ORS main finishing", LOGGER_NAME); }
        } catch (Exception ex) {
            LoggerAdapter.error("Stack trace", ex, LOGGER_NAME);
            LoggerAdapter.error(MessageKey.ERROR.getLocalizedMessage(), ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info(MessageKey.APP_EXIT.getLocalizedMessage(), LOGGER_NAME);
        }
    }
}

