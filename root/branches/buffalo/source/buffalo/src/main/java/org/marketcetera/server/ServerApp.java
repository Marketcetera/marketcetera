package org.marketcetera.server;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.core.ApplicationBase;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResource;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ServerApp
        extends ApplicationBase
        implements Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        running.set(true);
        startWaitingForever();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        running.set(false);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /**
     *
     *
     * @param args
     */
    public static void main(String[] args)
    {
        initializeLogger(LOGGER_CONF_FILE);
        theApp = new ServerApp();
        // make sure we can shut down cleanly upon halt
        registerShutdownHook();
        // TODO start message
        theApp.start();
        // TODO stop message
    }
    /**
     * 
     *
     *
     */
    private static void registerShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                theApp.stop();
            }
        });
    }
    /**
     * Initializes the logger for this application.
     *
     * @param inLogConfig a <code>String</code> value containing the name of the logger configuration file.
     */
    private static void initializeLogger(String inLogConfig)
    {
        PropertyConfigurator.configureAndWatch(ApplicationBase.CONF_DIR + inLogConfig,
                                               LOGGER_WATCH_DELAY);
    }
    /**
     * Create a new ServerApp instance.
     */
    private ServerApp()
    {
        configureSpring();
    }
    /**
     * 
     *
     *
     */
    private void configureSpring()
    {
        GenericApplicationContext parentContext = new GenericApplicationContext();
        mContext = parentContext;
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(parentContext);
        xmlReader.loadBeanDefinitions(new FileSystemResource(CONF_DIR + "server.xml"));
        parentContext.refresh();
        mContext.registerShutdownHook();
    }
    /**
     * 
     */
    private static ServerApp theApp;
    /**
     * 
     */
    private volatile ConfigurableApplicationContext mContext;
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
