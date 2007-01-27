package org.marketcetera.oms;

import org.marketcetera.core.*;
import org.marketcetera.quickfix.SessionAdmin;

import java.util.List;
import java.util.LinkedList;
import java.lang.management.ManagementFactory;

//import org.quickfixj.jmx.JmxExporter;
//import org.quickfixj.jmx.mbean.connector.ConnectorJmxExporter;
import org.springframework.context.ApplicationContext;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.JMException;

import quickfix.Session;
import quickfix.SocketInitiator;

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
 *   <li>{@link QuickFIXSender} = actually sends the FIX messages</li>
 * </ol>
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class OrderManagementSystem extends ApplicationBase {

    private static final String LOGGER_NAME = OrderManagementSystem.class.getName();
    public static final MessageBundleInfo OMS_MESSAGE_BUNDLE_INFO = new MessageBundleInfo("oms", "oms_messages");
    private static final String[] APP_CONTEXT_CONFIG_FILES = {"quickfixj.xml", "order-modifiers.xml", "quickfixj.appctx.xml", "oms.xml"};

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

            SocketInitiator initiator = (SocketInitiator) appCtx.getBean("socketInitiator", SocketInitiator.class);
            SessionAdmin adminBean = new SessionAdmin((Session)initiator.getManagedSessions().get(0));
            oms.registerMBean(adminBean, true);
/*
            JmxExporter exporter = new JmxExporter();
            exporter.enableStatistics();
            exporter.export(initiator);
            MBeanServer mbServer = exporter.getMBeanServer();
*/

            oms.startWaitingForever();
            if(LoggerAdapter.isDebugEnabled(LOGGER_NAME)) { LoggerAdapter.debug("OMS main finishing", LOGGER_NAME); }
        } catch (Exception ex) {
            LoggerAdapter.error(MessageKey.ERROR.getLocalizedMessage(), ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info(MessageKey.APP_EXIT.getLocalizedMessage(), LOGGER_NAME);
        }
    }
}

