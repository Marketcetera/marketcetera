package org.marketcetera.core;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/* $License$ */

/**
 * Provides a process-based application in which to run Marketcetera components.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class ApplicationContainer
        extends ApplicationBase
        implements ApplicationInfoProvider, Lifecycle, ApplicationContextAware
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getAppDir()
     */
    @Override
    public File getAppDir()
    {
        return new File(APP_DIR);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getConfDir()
     */
    @Override
    public File getConfDir()
    {
        return new File(CONF_DIR);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getContext()
     */
    @Override
    public ApplicationContext getContext()
    {
        return context;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ApplicationInfoProvider#getArguments()
     */
    @Override
    public String[] getArguments()
    {
        return arguments;
    }
    /**
     * Sets the application arguments value.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public void setArguments(String...inArgs)
    {
        arguments = inArgs;
    }
    /**
     * Starts application.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args)
    {
        // log application start
        Messages.APP_COPYRIGHT.info(ApplicationContainer.class);
        Messages.APP_VERSION_BUILD.info(ApplicationContainer.class,
                                        ApplicationVersion.getVersion(ApplicationContainer.class),
                                        ApplicationVersion.getBuildNumber());
        Messages.APP_START.info(ApplicationContainer.class);
        // check to see if we're using a different starting context file than the default
        String rawValue = StringUtils.trimToNull(System.getProperty(CONTEXT_FILE_PROP));
        if(rawValue != null) {
            contextFilename = rawValue;
        }
        final ApplicationContainer application;
        try {
            application = new ApplicationContainer();
            application.setArguments(args);
            application.start();
        } catch(Exception e) {
            if(exitCode == 0) {
                exitCode = -1;
            }
            e.printStackTrace();
            try {
                Messages.APP_STOP_ERROR.error(ApplicationContainer.class,
                                              e);
            } catch(Exception e2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                e2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                e.printStackTrace();
            }
            System.exit(exitCode);
            return;
        }
        Messages.APP_STARTED.info(ApplicationContainer.class);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                application.stop();
                Messages.APP_STOP.info(ApplicationContainer.class);
                if(LogManager.getContext() instanceof LoggerContext) {
                    Configurator.shutdown((LoggerContext)LogManager.getContext());
                }
            }
        });
        try {
            application.startWaitingForever();
        } catch(Exception e) {
            try {
                Messages.APP_STOP_ERROR.error(ApplicationContainer.class,
                                              e);
            } catch(Exception e2) {
                System.err.println("Reporting failed"); //$NON-NLS-1$
                System.err.println("Reporting failure"); //$NON-NLS-1$
                e2.printStackTrace();
                System.err.println("Original failure"); //$NON-NLS-1$
                e.printStackTrace();
            }
            System.exit(exitCode);
            return;
        }
        Messages.APP_STOP_SUCCESS.info(ApplicationContainer.class);
        System.exit(exitCode);
    }
    /**
     * Adds the given shutdown task to the shutdown task collection.
     * 
     * <p>Tasks are executed upon application shutdown in their native order.
     * 
     * @param inTask a <code>ShutdownTask</code> value
     */
    public synchronized static void addShutdownTask(ComparableTask inTask)
    {
        shutdownTasks.add(inTask);
    }
    /**
     * Removes the given shutdown task from the shutdown task collection.
     * 
     * @param inTask a <code>ShutdownTask</code> value
     */
    public synchronized static void removeShutdownTask(ComparableTask inTask)
    {
        shutdownTasks.remove(inTask);
    }
    /**
     * Stops the running instance from waiting.
     *
     * <p>Does not stop the instance, just interrupts the running loop.
     */
    public static void stopInstanceWaiting()
    {
        if(instance != null) {
            instance.stopWaitingForever();
        }
    }
    /**
     * Gets the arguments passed to the application if any.
     *
     * @return a <code>String[]</code> value or <code>null</code>
     */
    public static String[] getInstanceArguments()
    {
        if(instance == null) {
            return null;
        }
        return instance.getArguments();
    }
    /**
     * Get the instance value.
     *
     * @return an <code>ApplicationContainer</code> value
     */
    public static ApplicationContainer getInstance()
    {
        return instance;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        instance = this;
        context = parentContext;
        try {
            context = generateContext();
        } catch (Exception e) {
            SLF4JLoggerProxy.error(this,
                                   e,
                                   "Encountered startup problem");
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new RuntimeException(e);
            }
        }
        if(context instanceof AbstractApplicationContext) {
            ((AbstractApplicationContext)context).registerShutdownHook();
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inContext)
            throws BeansException
    {
        parentContext = inContext;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        try {
            for(ComparableTask task : shutdownTasks) {
                try {
                    task.run();
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "Error executing shutdown task: {}",
                                          task);
                }
            }
//            context.stop();
            context = null;
        } finally {
            running.set(false);
        }
    }
    /**
     * Sets the exit code that will be returned when the application quits.
     *
     * @param inExitCode an <code>int</code> value
     */
    public static void setExitCode(int inExitCode)
    {
        exitCode = inExitCode;
    }
    /**
     * Get the exitCode value.
     *
     * @return an <code>int</code> value
     */
    public static int getExitCode()
    {
        return exitCode;
    }
    /**
     * Generates the base application context with which to run.
     *
     * @return a <code>ConfigurableApplicationContext</code> value
     */
    protected ConfigurableApplicationContext generateContext()
    {
        return new FileSystemXmlApplicationContext(new String[] { "file:"+CONF_DIR+contextFilename }, //$NON-NLS-1$
                                                   parentContext);
    }
    /**
     * indicates if the app is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * arguments passed to the cmd line
     */
    private String[] arguments;
    /**
     * Spring application context
     */
    private ApplicationContext context;
    /**
     * singleton instance of the application container
     */
    private static ApplicationContainer instance;
    /**
     * collection of tasks to run upon application shutdown
     */
    private static final Set<ComparableTask> shutdownTasks = new TreeSet<ComparableTask>();
    /**
     * optional command-line parameter that indicates a different context file to use
     */
    public static final String CONTEXT_FILE_PROP = "org.marketcetera.contextFile"; //$NON-NLS-1$
    /**
     * indicates the name of the context file to use
     */
    private static String contextFilename = "application.xml";
    /**
     * exit code to return on exit
     */
    protected static int exitCode = 0;
    /**
     * parent application context value
     */
    private ApplicationContext parentContext;
}
