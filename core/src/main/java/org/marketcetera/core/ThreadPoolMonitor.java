package org.marketcetera.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Monitors a thread pool.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ThreadPoolMonitor
{
    /**
     * Create a new ThreadPoolMonitor instance.
     */
    public ThreadPoolMonitor() {}
    /**
     * Create a new ThreadPoolMonitor instance.
     *
     * @param inThreadPool a <code>ThreadPoolExecutor</code> value
     * @param inName a <code>String</code> value
     */
    public ThreadPoolMonitor(ThreadPoolExecutor inThreadPool,
                             String inName)
    {
        executor = inThreadPool;
        name = inName;
        Validate.notNull(executor);
        Validate.notNull(name);
    }
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(executor);
        Validate.notNull(name);
        monitorService = Executors.newSingleThreadScheduledExecutor();
        monitorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    Messages.THREAD_POOL_MONITOR.info(ThreadPoolMonitor.this,
                                                      name,
                                                      executor.getActiveCount(),
                                                      executor.getMaximumPoolSize(),
                                                      executor.getLargestPoolSize(),
                                                      executor.getCompletedTaskCount(),
                                                      executor.getPoolSize(),
                                                      executor.getTaskCount());
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(ThreadPoolMonitor.this,
                                          e);
                }
            }
        },monitorInterval,monitorInterval,TimeUnit.MILLISECONDS);
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        monitorService.shutdownNow();
    }
    /**
     * Get the executor value.
     *
     * @return a <code>ThreadPoolExecutor</code> value
     */
    public ThreadPoolExecutor getExecutor()
    {
        return executor;
    }
    /**
     * Sets the executor value.
     *
     * @param inExecutor a <code>ThreadPoolExecutor</code> value
     */
    public void setExecutor(ThreadPoolExecutor inExecutor)
    {
        executor = inExecutor;
    }
    /**
     * Get the monitorInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getMonitorInterval()
    {
        return monitorInterval;
    }
    /**
     * Sets the monitorInterval value.
     *
     * @param a <code>long</code> value
     */
    public void setMonitorInterval(long inMonitorInterval)
    {
        monitorInterval = inMonitorInterval;
    }
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * human-readable name of the executor to monitor
     */
    private String name;
    /**
     * executor to monitor
     */
    private ThreadPoolExecutor executor;
    /**
     * interval at which to monitor (in  ms)
     */
    private long monitorInterval = 10000;
    /**
     * monitors the pool
     */
    private ScheduledExecutorService monitorService;
}
