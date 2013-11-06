package org.marketcetera.core;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.PropertyConfigurator;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/* $License$ */

/**
 * Provides a process-based application in which to run Marketcetera components.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ApplicationContainer
        extends ApplicationBase
{
    /**
     * Starts application.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args)
    {
        // configure logger
        PropertyConfigurator.configureAndWatch(ApplicationBase.CONF_DIR+"log4j.properties",
                                               LOGGER_WATCH_DELAY);
        // log application start
        Messages.APP_COPYRIGHT.info(ApplicationContainer.class);
        Messages.APP_VERSION_BUILD.info(ApplicationContainer.class,
                                        ApplicationVersion.getVersion(),
                                        ApplicationVersion.getBuildNumber());
        Messages.APP_START.info(ApplicationContainer.class);
        final ApplicationContainer application;
        try {
            application = new ApplicationContainer(args);
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
            return;
        }
        Messages.APP_STARTED.info(ApplicationContainer.class);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                application.stop();
                Messages.APP_STOP.info(ApplicationContainer.class);
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
            return;
        }
        Messages.APP_STOP_SUCCESS.info(ApplicationContainer.class);
    }
    /**
     * Get the context value.
     *
     * @return a <code>ConfigurableApplicationContext</code> value
     */
    public ConfigurableApplicationContext getContext()
    {
        return context;
    }
    /**
     * Adds the given shutdown task to the shutdown task collection.
     * 
     * <p>Tasks are executed upon application shutdown in their native order.
     * 
     * @param inTask a <code>ShutdownTask</code> value
     */
    public void addShutdownTask(ComparableTask inTask)
    {
        shutdownTasks.add(inTask);
    }
    /**
     * Removes the given shutdown task from the shutdown task collection.
     * 
     * @param inTask a <code>ShutdownTask</code> value
     */
    public void removeShutdownTask(ComparableTask inTask)
    {
        shutdownTasks.remove(inTask);
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
    /**
     * Executed when the application stops.
     */
    private void stop()
    {
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
        context.stop();
    }
    /**
     * Create a new ApplicationContainer instance.
     *
     * @param inArgs a <code>String[]</code> value
     */
    private ApplicationContainer(String[] inArgs)
    {
        instance = this;
        context = new FileSystemXmlApplicationContext(new String[] { "file:"+CONF_DIR+"application.xml" }, //$NON-NLS-1$ //$NON-NLS-2$
                                                      null);
    }
    /**
     * Spring application context
     */
    private ConfigurableApplicationContext context;
    /**
     * singleton instance of the application container
     */
    private static ApplicationContainer instance;
    /**
     * collection of tasks to run upon application shutdown
     */
    private final Set<ComparableTask> shutdownTasks = new TreeSet<ComparableTask>();
}
