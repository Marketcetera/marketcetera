package org.marketcetera.server;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.server.config.ServerConfig;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

/* $License$ */

/**
 * Provides Marketcetera server-space services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ServerApp
        extends ApplicationBase
        implements Lifecycle
{
    /**
     * Gets the <code>Server</code> instance.
     *
     * @return a <code>Server</code> instance
     * @throws IllegalStateException if the <code>Server</code> has not been initialized
     */
    public static ServerApp getInstance()
    {
        if(instance == null) {
            throw new IllegalStateException("The server has not been initialized");
        }
        return instance;
    }
    /**
     *
     *
     * @param inArgs
     */
    public static void main(String[] inArgs)
    {
        // Configure logger.
        PropertyConfigurator.configureAndWatch(String.format("%slog4j%sserver.properties",
                                                             ApplicationBase.CONF_DIR,
                                                             File.separator),
                                               LOGGER_WATCH_DELAY);
        // Log application start.
        Messages.APP_COPYRIGHT.info(LOGGER_CATEGORY);
        Messages.APP_VERSION_BUILD.info(LOGGER_CATEGORY,
                                        ApplicationVersion.getVersion(),
                                        ApplicationVersion.getBuildNumber());
        Messages.APP_START.info(LOGGER_CATEGORY);
        // Start application
        final ServerApp app;
        try {
            app = new ServerApp(inArgs);
        } catch(Exception t) {
            try {
                Messages.APP_STOP_ERROR.error(LOGGER_CATEGORY,
                                              t);
            } catch (Throwable t2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                t2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                t.printStackTrace();
            }
            return;
        }
        Messages.APP_STARTED.info(LOGGER_CATEGORY);
        // Hook to log shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                app.stop();
                Messages.APP_STOP.info(LOGGER_CATEGORY);
            }
        });
        // Execute application.
        try {
            app.startWaitingForever();
        } catch(Exception t) {
            try {
                Messages.APP_STOP_ERROR.error(LOGGER_CATEGORY,
                                              t);
            } catch (Throwable t2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                t2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                t.printStackTrace();
            }
            return;
        }
        Messages.APP_STOP_SUCCESS.info(LOGGER_CATEGORY);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        // TODO Auto-generated method stub
    }
    /**
     * Create a new ServerApp instance.
     *
     * @param inArguments
     * @throws Exception 
     */
    private ServerApp(String[] inArguments)
            throws Exception
    {
        if(inArguments.length != 0) {
            printUsage(Messages.APP_NO_ARGS_ALLOWED);
        }
        // build base Spring configuration
        StaticApplicationContext parentContext = new StaticApplicationContext(new FileSystemXmlApplicationContext(APP_CONTEXT_CFG_BASE));
        parentContext.refresh();
        // read the configuration specified in server.xml
        context = new FileSystemXmlApplicationContext(new String[] { "file:"+CONF_DIR+ //$NON-NLS-1$
                                                                      "server.xml"}, //$NON-NLS-1$
                                                       parentContext);
        // instantiate the objects specified in the configuration
        context.start();
        // create resource managers
        ServerConfig cfg = ServerConfig.getInstance();
        if(cfg == null) {
            throw new I18NException(Messages.APP_NO_CONFIGURATION);
        }
    }
    /**
     * Prints the given message alongside usage information on the
     * standard error stream, and throws an exception.
     *
     * @param message The message.
     *
     * @throws IllegalStateException Always thrown.
     */
    private void printUsage(I18NBoundMessage message)
            throws I18NException
    {
        System.err.println(message.getText());
        System.err.println(Messages.APP_USAGE.getText
                           (ServerApp.class.getName()));
        System.err.println(Messages.APP_AUTH_OPTIONS.getText());
        System.err.println();
        throw new I18NException(message);
    }
    /**
     * 
     */
    private static final Class<?> LOGGER_CATEGORY = ServerApp.class;
    /**
     * 
     */
    private static final String APP_CONTEXT_CFG_BASE= "file:" + CONF_DIR + "properties.xml";
    /**
     * the singleton instance of the server
     */
    private static volatile ServerApp instance;
    private final AbstractApplicationContext context;
}
