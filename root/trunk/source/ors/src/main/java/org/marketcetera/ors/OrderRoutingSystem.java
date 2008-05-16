package org.marketcetera.ors;

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
 * $Id: OrderRoutingSystem.java 3587 2008-04-24 23:38:47Z tlerios $
 */
@ClassVersion("$Id: OrderRoutingSystem.java 3587 2008-04-24 23:38:47Z tlerios $")
public class OrderRoutingSystem extends ApplicationBase {

    private static final String LOGGER_NAME = OrderRoutingSystem.class.getName();
    public static final MessageBundleInfo ORS_MESSAGE_BUNDLE_INFO = new MessageBundleInfo("ors", "ors_messages");
    public static final String[] APP_CONTEXT_CONFIG_FILES = {"quickfixj.xml", "message-modifiers.xml",
            "order-limits.xml", "ors.xml", "ors-shared.xml"};

    protected List<MessageBundleInfo> getLocalMessageBundles() {
        LinkedList<MessageBundleInfo> bundles = new LinkedList<MessageBundleInfo>();
        bundles.add(ORS_MESSAGE_BUNDLE_INFO);
        return bundles;
    }

    public static void main(String [] args) throws ConfigFileLoadingException
    {
        try {
            OrderRoutingSystem ors = new OrderRoutingSystem();
            ApplicationContext appCtx = ors.createApplicationContext(APP_CONTEXT_CONFIG_FILES, true);

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
            LoggerAdapter.error(MessageKey.ERROR.getLocalizedMessage(), ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info(MessageKey.APP_EXIT.getLocalizedMessage(), LOGGER_NAME);
        }
    }
}

