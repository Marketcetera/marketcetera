package org.marketcetera.oms;

import org.marketcetera.core.*;

import java.util.List;
import java.util.LinkedList;

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

    protected List<MessageBundleInfo> getLocalMessageBundles() {
        LinkedList<MessageBundleInfo> bundles = new LinkedList<MessageBundleInfo>();
        bundles.add(OMS_MESSAGE_BUNDLE_INFO);
        return bundles;
    }


    public static void main(String [] args) throws ConfigFileLoadingException
    {
        try {
            OrderManagementSystem oms = new OrderManagementSystem();
            oms.createApplicationContext("oms.xml", true);
            oms.startWaitingForever();
            if(LoggerAdapter.isDebugEnabled(LOGGER_NAME)) { LoggerAdapter.debug("OMS main finishing", LOGGER_NAME); }
        } catch (Exception ex) {
            LoggerAdapter.error(MessageKey.ERROR.getLocalizedMessage(), ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info(MessageKey.APP_EXIT.getLocalizedMessage(), LOGGER_NAME);
        }
    }
}

