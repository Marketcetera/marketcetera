package org.marketcetera.pool.impl;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.pool.ExecutorServiceProvider;
import org.marketcetera.pool.PriorityRunnable;

/* $License$ */

/**
 * Provides a common thread pool with optional job prioritization.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ExecutorServiceProviderImpl
        implements ExecutorServiceProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.pool.ExecutorServiceProvider#submit(org.marketcetera.pool.PriorityRunnable)
     */
    @Override
    public void execute(PriorityRunnable inJob)
    {
        jobProcessingPool.execute(inJob);
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        jobProcessingPool = new ThreadPoolExecutor(corePoolSize,
                                                   maxPoolSize,
                                                   keepAliveTime,
                                                   TimeUnit.MILLISECONDS,
                                                   new PriorityBlockingQueue<Runnable>());
        jobProcessingPool.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        if(jobProcessingPool != null) {
            try {
                jobProcessingPool.shutdownNow();
            } catch (Exception ignored) {}
            jobProcessingPool = null;
        }
    }
    /**
     * Get the maxPoolSize value.
     *
     * @return an <code>int</code> value
     */
    public int getMaxPoolSize()
    {
        return maxPoolSize;
    }
    /**
     * Sets the maxPoolSize value.
     *
     * @param an <code>int</code> value
     */
    public void setMaxPoolSize(int inMaxPoolSize)
    {
        maxPoolSize = inMaxPoolSize;
    }
    /**
     * Get the allowCoreThreadTimeOut value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getAllowCoreThreadTimeOut()
    {
        return allowCoreThreadTimeOut;
    }
    /**
     * Sets the allowCoreThreadTimeOut value.
     *
     * @param a <code>boolean</code> value
     */
    public void setAllowCoreThreadTimeOut(boolean inAllowCoreThreadTimeOut)
    {
        allowCoreThreadTimeOut = inAllowCoreThreadTimeOut;
    }
    /**
     * Get the corePoolSize value.
     *
     * @return an <code>int</code> value
     */
    public int getCorePoolSize()
    {
        return corePoolSize;
    }
    /**
     * Sets the corePoolSize value.
     *
     * @param an <code>int</code> value
     */
    public void setCorePoolSize(int inCorePoolSize)
    {
        corePoolSize = inCorePoolSize;
    }
    /**
     * Get the keepAliveTime value.
     *
     * @return an <code>int</code> value
     */
    public int getKeepAliveTime()
    {
        return keepAliveTime;
    }
    /**
     * Sets the keepAliveTime value.
     *
     * @param an <code>int</code> value
     */
    public void setKeepAliveTime(int inKeepAliveTime)
    {
        keepAliveTime = inKeepAliveTime;
    }
    /**
     * processes incoming messages
     */
    private ThreadPoolExecutor jobProcessingPool;
    /**
     * the maximum number of threads to allow in the pool
     */
    private int maxPoolSize = 100;
    /**
     * indicates if unused threads are allowed to timeout
     */
    private boolean allowCoreThreadTimeOut = false;
    /**
     * the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
     */
    private int corePoolSize = 1;
    /**
     * when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating
     */
    private int keepAliveTime = 10000;
}
