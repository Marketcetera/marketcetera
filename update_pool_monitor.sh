#!/bin/bash
# Script to update PoolMonitor to use HikariCP

cat > /tmp/PoolMonitor.java <<'EOF'
package org.marketcetera.persist;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariDataSource;

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
        final String dataSourceName = pool.getPoolName();
        monitorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("DatabasePoolMonitor").build());
        monitorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    int numBusy = pool.getHikariPoolMXBean().getActiveConnections();
                    int numConnections = pool.getMaximumPoolSize();
                    int numIdle = pool.getHikariPoolMXBean().getIdleConnections();
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
     * @return a <code>HikariDataSource</code> value
     */
    public HikariDataSource getPool()
    {
        return pool;
    }
    /**
     * Sets the pool value.
     *
     * @param inPool a <code>HikariDataSource</code> value
     */
    public void setPool(HikariDataSource inPool)
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
     * @param inMonitorInterval a <code>long</code> value
     */
    public void setMonitorInterval(long inMonitorInterval)
    {
        monitorInterval = inMonitorInterval;
    }
    /**
     * pool source to monitor
     */
    private HikariDataSource pool;
    /**
     * interval at which to monitor (in  ms)
     */
    private long monitorInterval = 10000;
    /**
     * monitors the pool
     */
    private ScheduledExecutorService monitorService;
}
EOF

# Replace the original file
mv /tmp/PoolMonitor.java /Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera/core/src/main/java/org/marketcetera/persist/PoolMonitor.java

echo "PoolMonitor updated to use HikariCP"