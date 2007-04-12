package org.marketcetera.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Abstract superclass to all applications
 * Initializes the logger and registers with the MBean server for JMX introspection
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public abstract class ApplicationBase implements Clock {

    protected static LoggerAdapter sLogger;
    private ClassPathXmlApplicationContext appCtx;
    protected FIXMessageFactory msgFactory;
    protected FIXVersion fixVersion;
    protected FIXDataDictionary fixDD;
    private static final String FIX_VERSION_NAME = "fixVersionEnum";

    public ApplicationBase()
    {
        List<MessageBundleInfo> bundles = getLocalMessageBundles();
        MessageBundleManager.registerCoreMessageBundle();
        if (bundles != null){
            for (MessageBundleInfo messageBundleInfo : bundles) {
                MessageBundleManager.registerMessageBundle(messageBundleInfo);
            }
        }
        sLogger = LoggerAdapter.initializeLogger("mktctrRoot");
    }

    public ApplicationContext createApplicationContext(String[] ctxFileNames, boolean registerShutdownHook) throws MarketceteraException {
        appCtx = new ClassPathXmlApplicationContext(ctxFileNames) {
            protected void onClose() {
                if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("in shutdown hook", this); }
                super.onClose();
            }
        };

        if(registerShutdownHook) {
            appCtx.registerShutdownHook();
        }
        fixVersion = (FIXVersion) appCtx.getBean(FIX_VERSION_NAME);
        fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        msgFactory = fixVersion.getMessageFactory();
        return appCtx;
    }

    /** Create a semaphor and wait for it forever (noone will ever signal it).
     * This is to put the app in a loop if the app is written as a "receiver"
     * You quit the app by either killing the process or pressing Ctrl-C.
     */
    public void startWaitingForever()
    {
        try {
            if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Starting to wait forever", this); }
            new Semaphore(0).acquire();
        } catch (InterruptedException e) {
            LoggerAdapter.debug("Exception in sema wait", e, this);
        }
    }

    /** Get the FIX version associated with this application */
    public FIXVersion getFIXVersion()
    {
        return fixVersion;
    }

    /* (non-Javadoc)
      * @see Clock#getTime()
      */
    public long getTime() {
        return System.currentTimeMillis();
    }

    /* (non-Javadoc)
    * @see Clock#getApproximateTime
    */
    public long getApproximateTime() {
        // TODO: make this read a variable that is updated periodically by a thread
        return System.currentTimeMillis();
    }

    /** Subclasses can override the implementation if they need to add additoinal or specific
     * message bundles for internationalization.
     */
    protected abstract List<MessageBundleInfo> getLocalMessageBundles();

    /** Returns a pointer to the Spring application context that started this app */
    public ClassPathXmlApplicationContext getAppCtx() {
        return appCtx;
    }
}
