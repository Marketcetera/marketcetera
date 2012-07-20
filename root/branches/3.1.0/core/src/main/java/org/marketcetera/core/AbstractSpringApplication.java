package org.marketcetera.core;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.Validate;
import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/* $License$ */

/**
 * Provides services for non-web Spring Application classes.
 * 
 * <p>Subclasses may extend this class to act as an executable application. Create
 * a subclass with a static main method. In that method, instantiate this object,
 * {@link #start start} it, and invoke {@link #waitForever()}. 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractSpringApplication.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id: AbstractSpringApplication.java 82384 2012-07-20 19:09:59Z colin $")
public abstract class AbstractSpringApplication
        implements Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public final boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized final void start()
    {
        if(isRunning()) {
            return;
        }
        try {
            // start logger
            PropertyConfigurator.configureAndWatch(CONF_DIR + getLoggerFilename(),
                                                   LOGGER_WATCH_DELAY);
            // begin start process
            doStartingMessage();
            // add shutdown hook that calls stop on the application object
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        AbstractSpringApplication.this.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
            // load Spring config
            context = createContext();
            Validate.notNull(context,
                             Messages.NULL_CONTEXT.getText(getName()));
            context.registerShutdownHook();
            // perform any user-specified behavior
            doStart();
            // start Spring config
            if(!isContextAutostarted()) {
                context.refresh();
                context.start();
            }
            // start process complete
            doStartedMessage();
            // all done
            running.set(true);
        } catch (RuntimeException e) {
            Messages.APP_START_ERROR.error(getLoggerCategory(),
                                           e,
                                           getName());
            throw e;
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized final void stop()
    {
        if(!isRunning()) {
            return;
        }
        try {
            doStoppingMessage();
            try {
                doStop();
            } catch (Exception e) {
                // TODO
                e.printStackTrace();
            }
            context.stop();
        } finally {
            doStoppedMessage();
            running.set(false);
        }
    }
    /**
     * Gets the application context.
     *
     * @return a <code>ConfigurableApplicationContext</code> value
     */
    public final ConfigurableApplicationContext getContext()
    {
        return context;
    }
    /**
     * Causes the application to block until interrupted or stopped with {@link #stop()}.
     * 
     * <p>Use this method to cause the application object to stay alive
     * indefinitely, as in a static main method.
     *
     * @throws InterruptedException if the thread is interrupted which waiting
     */
    public final void waitForever()
            throws InterruptedException
    {
        if(!isRunning()) {
            throw new IllegalStateException();
        }
        while(isRunning()){
            synchronized(this) {
                wait(250);
            }
        }
    }
    /**
     * Gets the logger category to use for application messages.
     *
     * @return a <code>Class&lt;? extends AbstractSpringApplication&gt;</code> value
     */
    protected abstract Class<? extends AbstractSpringApplication> getLoggerCategory();
    /**
     * Gets the human-readable name to use to describe this application.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getName();
    /**
     * Indicates if the context used auto-starts itself or needs to be started.
     *
     * <p>The default behavior is true, that is, the context used starts itself.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isContextAutostarted()
    {
        return true;
    }
    /**
     * Gets the logger filename to use.
     * 
     * <p>Subclasses may override this method to specify a different log file name
     * to use than the default.
     *
     * @return a <code>String</code>
     */
    protected String getLoggerFilename()
    {
        return LOGGER_CONF_FILE;
    }
    /**
     * Executes the starting message for this application.
     * 
     * <p>Subclasses may override this method to display a specialized start message.
     *
     * <p>If this method throws an exception, the application will not
     * start.
     */
    protected void doStartingMessage()
    {
        Messages.APP_COPYRIGHT.info(getLoggerCategory());
        Messages.APP_VERSION_BUILD.info(getLoggerCategory(),
                                        getName(),
                                        ApplicationVersion.getVersion(),
                                        ApplicationVersion.getBuildNumber());
        Messages.APP_STARTING.info(getLoggerCategory(),
                                getName());
    }
    /**
     * Displays a message when the application is started.
     */
    protected void doStartedMessage()
    {
        Messages.APP_STARTED.info(getLoggerCategory(),
                                  getName());
    }
    /**
     * Displays a message when the application is stopping. 
     */
    protected void doStoppingMessage()
    {
        Messages.APP_STOPPING.info(getLoggerCategory(),
                               getName());
    }
    /**
     * Displays a message when the application is stopped. 
     */
    protected void doStoppedMessage()
    {
        Messages.APP_STOPPED.info(getLoggerCategory(),
                                  getName());
    }
    /**
     * Executed during application start.
     * 
     * <p>Subclasses may override this method to provide their own
     * start behavior. The default implementation does nothing.
     * 
     * <p>If this method throws an exception, the application will not
     * start.
     */
    protected void doStart()
    {
    }
    /**
     * Executed during application stop.
     * 
     * <p>Subclasses may override this method to provide theor own
     * stop behavior. The default implementation does nothing.
     * 
     * <p>This method is executed before the context is stopped.
     *
     * <p>Exceptions thrown by this method will be ignored.
     */
    protected void doStop()
    {
    }
    /**
     * Creates the application context.
     * 
     * <p>Subclasses may override this method to provide their own
     * context. The returned context must be non-null and will
     * be refreshed and started after this method returns.
     *
     * <p>If this method throws an exception, the application will not
     * start.
     * 
     * @return a <code>ConfigurableApplicationContext</code>
     */
    protected ConfigurableApplicationContext createContext()
    {
        ConfigurableApplicationContext ctx;
        String contextFilename = AbstractSpringApplication.CONF_DIR + "main.xml";
        File contextFilenameFile = new File(contextFilename);
        if(contextFilenameFile.exists() &&
           contextFilenameFile.canRead()) {
            ctx = new FileSystemXmlApplicationContext(contextFilename);
        } else {
            SLF4JLoggerProxy.debug(getLoggerCategory(),
                                   "No file system context file at {}, checking class path for main.xml",
                                   contextFilenameFile.getAbsolutePath());
            ctx = new ClassPathXmlApplicationContext(new String[] { "main.xml" } );
        }
        return ctx;
    }
    /**
     * Create a new AbstractSpringApplication instance.
     */
    protected AbstractSpringApplication()
    {
        instance = this;
    }
    /**
     * default logger configuration file
     */
    public static final String LOGGER_CONF_FILE = "log4j.properties";
    /**
     * interval in ms at which logger properties file is refreshed 
     */
    public static final int LOGGER_WATCH_DELAY = 10 * 1000;
    /**
     * property to set to indicate the application dir on startup
     */
    public static final String APP_DIR_PROP = "org.marketcetera.appDir";
    /**
     * application directory value
     */
    public static final String APP_DIR;
    /**
     * configuration directory value 
     */
    public static final String CONF_DIR;
    /**
     * instance of the most recently created <code>AbstractSpringApplication</code> or <code>null</code>
     */
    public static volatile AbstractSpringApplication instance;
    /**
     * indicates if the application is running 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * context value underlying the application, guaranteed to be non-null if {@link #isRunning()}
     */
    private volatile ConfigurableApplicationContext context;
    /**
     * Static initialization common to all applications
     */
    static
    {
        String appDir = System.getProperty(APP_DIR_PROP);
        if (appDir == null) {
            appDir = "src" + File.separator + "test" + File.separator + "sample_data";
        }
        if (!appDir.endsWith(File.separator)) {
            appDir += File.separator;
        }
        APP_DIR = appDir;
        CONF_DIR = APP_DIR + "conf" + File.separator;
    }
}
