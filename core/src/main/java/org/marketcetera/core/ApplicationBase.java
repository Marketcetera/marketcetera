package org.marketcetera.core;

import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Abstract superclass to all applications
 * Initializes the logger and registers with the MBean server for JMX introspection
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ApplicationBase implements Clock {

    public static final String LOGGER_CONF_FILE = "log4j.properties"; //$NON-NLS-1$
    public static final int LOGGER_WATCH_DELAY = 20*1000;

    public static final String APP_DIR_PROP="org.marketcetera.appDir"; //$NON-NLS-1$
    public static final String APP_DIR;
    public static final String CONF_DIR;

    static {
        String appDir=System.getProperty(APP_DIR_PROP);
        if (appDir==null) {
            appDir="src"+File.separator+"test"+File.separator+"sample_data"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        if (!appDir.endsWith(File.separator)) {
            appDir+=File.separator;
        }
        APP_DIR=appDir;
        CONF_DIR=APP_DIR+"conf"+File.separator; //$NON-NLS-1$
    }

    public static final String USERNAME_BEAN_NAME="runtimeUsername"; //$NON-NLS-1$
    public static final String PASSWORD_BEAN_NAME="runtimePassword"; //$NON-NLS-1$

    private ClassPathXmlApplicationContext appCtx;
    protected FIXMessageFactory msgFactory;
    protected FIXVersion fixVersion;
    protected FIXDataDictionary fixDD;
    private boolean waitingForever = false;
    private static final String FIX_VERSION_NAME = "fixVersionEnum"; //$NON-NLS-1$

    private static final class MyApplicationContext
        extends ClassPathXmlApplicationContext
    {
        MyApplicationContext
            (String[] paths)
        {
            super(paths);
        }

        MyApplicationContext
            (String[] paths,
             ApplicationContext parent)
        {
            super(paths,parent);
        }

        protected void onClose()
        {
            SLF4JLoggerProxy.debug(this, "in shutdown hook"); //$NON-NLS-1$
            super.onClose();
        }
    };

    public ConfigurableApplicationContext createApplicationContext
        (String[] ctxFileNames,
         ApplicationContext parent,
         boolean registerShutdownHook)
        throws CoreException
    {
        if (parent==null) {
            appCtx=new MyApplicationContext(ctxFileNames);
        } else {
            appCtx=new MyApplicationContext(ctxFileNames,parent);
        }

        if(registerShutdownHook) {
            appCtx.registerShutdownHook();
        }
        fixVersion = (FIXVersion) appCtx.getBean(FIX_VERSION_NAME);
        fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        msgFactory = fixVersion.getMessageFactory();
        return appCtx;
    }

    public ConfigurableApplicationContext createApplicationContext
        (String[] ctxFileNames,
         boolean registerShutdownHook)
        throws CoreException
    {
        return createApplicationContext(ctxFileNames,null,registerShutdownHook);
    }

    /** Create a semaphor and wait for it forever (noone will ever signal it).
     * This is to put the app in a loop if the app is written as a "receiver"
     * You quit the app by either killing the process or pressing Ctrl-C.
     */
    public void startWaitingForever()
    {
        waitingForever = true;
        try {
            SLF4JLoggerProxy.debug(this, "Starting to wait forever"); //$NON-NLS-1$
            new Semaphore(0).acquire();
        } catch (InterruptedException e) {
            SLF4JLoggerProxy.debug(this, e, "Exception in sema wait"); //$NON-NLS-1$
        } finally {
            waitingForever = false;
        }
    }

    /**
     * Returns true if the application is running
     * the {@link #startWaitingForever()} method
     * @return true if the application is running a
     * thread thats waiting for ever.
     */
    public boolean isWaitingForever() {
        return waitingForever;
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
        return System.currentTimeMillis(); //i18n_datetime
    }

    /* (non-Javadoc)
    * @see Clock#getApproximateTime
    */
    public long getApproximateTime() {
        // TODO: make this read a variable that is updated periodically by a thread
        return System.currentTimeMillis(); //i18n_datetime
    }

    /** Returns a pointer to the Spring application context that started this app */
    public ClassPathXmlApplicationContext getAppCtx() {
        return appCtx;
    }
}
