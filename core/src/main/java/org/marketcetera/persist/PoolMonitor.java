package org.marketcetera.persist;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

/* $License$ */

/**
 * Monitors a database pool.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PoolMonitor
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(pool);
        final String dataSourceName = pool.getDataSourceName();
        monitorService = Executors.newSingleThreadScheduledExecutor();
        monitorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    int numBusy = pool.getNumBusyConnectionsAllUsers();
                    int numConnections = pool.getNumConnectionsAllUsers();
                    if(pool instanceof ComboPooledDataSource) {
                        numConnections = ((ComboPooledDataSource)pool).getMaxPoolSize();
                    }
                    int numIdle = pool.getNumIdleConnectionsAllUsers();
                    Messages.POOL_MONITOR_STATS.info(PoolMonitor.this,
                                                     dataSourceName,
                                                     numIdle,
                                                     numBusy,
                                                     numConnections);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(PoolMonitor.this,
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
     * Get the pool value.
     *
     * @return a <code>PooledDataSource</code> value
     */
    public PooledDataSource getPool()
    {
        return pool;
    }
    /**
     * Sets the pool value.
     *
     * @param a <code>PooledDataSource</code> value
     */
    public void setPool(PooledDataSource inPool)
    {
        pool = inPool;
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
     * pool source to monitor
     */
    private PooledDataSource pool;
    /**
     * interval at which to monitor (in  ms)
     */
    private long monitorInterval = 10000;
    /**
     * monitors the pool
     */
    private ScheduledExecutorService monitorService;
}
