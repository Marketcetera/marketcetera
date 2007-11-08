package org.marketcetera.oms;

import org.marketcetera.core.*;
import org.marketcetera.oms.mbeans.OMSAdmin;
import org.quickfixj.jmx.JmxExporter;
import org.springframework.context.ApplicationContext;
import quickfix.SocketInitiator;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * OrderManagementSystem
 * Main entrypoint for sending orders and receiving responses from a FIX engine
 *
 * The OMS is configured using Spring, using the following modules:
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
public class OrderManagementSystem extends ApplicationBase {

    private static final String LOGGER_NAME = OrderManagementSystem.class.getName();
    public static final MessageBundleInfo OMS_MESSAGE_BUNDLE_INFO = new MessageBundleInfo("oms", "oms_messages");
    public static final String[] APP_CONTEXT_CONFIG_FILES = {"quickfixj.xml", "message-modifiers.xml",
            "order-limits.xml", "oms.xml", "oms-shared.xml"};

    protected List<MessageBundleInfo> getLocalMessageBundles() {
        LinkedList<MessageBundleInfo> bundles = new LinkedList<MessageBundleInfo>();
        bundles.add(OMS_MESSAGE_BUNDLE_INFO);
        return bundles;
    }

    public static void main(String [] args) throws ConfigFileLoadingException
    {
        try {
            OrderManagementSystem oms = new OrderManagementSystem();
            ApplicationContext appCtx = oms.createApplicationContext(APP_CONTEXT_CONFIG_FILES, true);

            if(LoggerAdapter.isInfoEnabled(LOGGER_NAME)) {
                String connectHost = (String) appCtx.getBean("socketConnectHostValue", String.class);
                String connectPort = (String) appCtx.getBean("socketConnectPortValue", String.class);
                LoggerAdapter.info(OMSMessageKey.CONNECTING_TO.getLocalizedMessage(connectHost, connectPort), LOGGER_NAME);
            }

            SocketInitiator initiator = (SocketInitiator) appCtx.getBean("socketInitiator", SocketInitiator.class);
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            JmxExporter exporter = new JmxExporter(mbeanServer);
            exporter.export(initiator);
            OMSAdmin omsAdmin = (OMSAdmin) appCtx.getBean("omsAdmin", OMSAdmin.class);
            mbeanServer.registerMBean(omsAdmin, new ObjectName("org.marketcetera.oms.mbean:type=OMSAdmin"));

            oms.startWaitingForever();
            if(LoggerAdapter.isDebugEnabled(LOGGER_NAME)) { LoggerAdapter.debug("OMS main finishing", LOGGER_NAME); }
        } catch (Exception ex) {
            LoggerAdapter.error(MessageKey.ERROR.getLocalizedMessage(), ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info(MessageKey.APP_EXIT.getLocalizedMessage(), LOGGER_NAME);
        }
    }
}

