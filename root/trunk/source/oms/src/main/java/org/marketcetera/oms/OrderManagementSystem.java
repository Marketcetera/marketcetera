package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigFileLoadingException;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageBundleInfo;
import org.marketcetera.core.MessageBundleManager;
import org.marketcetera.core.MessageKey;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * OrderManagementSystem
 * Main entrypoint for sending orders and receiving responses from a FIX engine
 *
 * <pre>
 * The OMS consists of the following JCyclone configuration:
 *
 * FIXSessionAdapterSource  ------|
 *                                |--> OrderManager ---> OutputStage
 * JMSAdapterSource         ------|
 *</pre>
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class OrderManagementSystem {

    private static final String LOGGER_NAME = OrderManagementSystem.class.getName();
    public static final MessageBundleInfo OMS_MESSAGE_BUNDLE_INFO = new MessageBundleInfo("oms", "oms_messages");

    protected static OrderManagementSystem sOMS = null;
	private static LoggerAdapter sLogger;


    protected OrderManagementSystem()
    {
    }


    public static void init()
    {
        MessageBundleManager.registerCoreMessageBundle();
        MessageBundleManager.registerMessageBundle(OMS_MESSAGE_BUNDLE_INFO);
        sLogger = LoggerAdapter.initializeLogger("mktctrRoot");
    }




    public static void main(String [] args) throws ConfigFileLoadingException
    {
    	init();
        try {
        	ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("oms.xml");
            appCtx.registerShutdownHook();
//            appCtx.start();
            System.in.read();
        } catch (Exception ex) {
            LoggerAdapter.error(MessageKey.ERROR.getLocalizedMessage(), ex, LOGGER_NAME);
        } finally {
            LoggerAdapter.info(MessageKey.APP_EXIT.getLocalizedMessage(), LOGGER_NAME);
        }
    }

    public static OrderManagementSystem getOMS() { return sOMS; }



}

