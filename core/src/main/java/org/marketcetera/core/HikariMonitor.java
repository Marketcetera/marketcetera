package org.marketcetera.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.NotificationExecutor;
import org.marketcetera.metrics.MetricService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

/* $License$ */

/**
 * Provides a monitor for the Hikari connection pool.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@EnableAutoConfiguration
public class HikariMonitor
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {} with interval {} and threshold {}",
                              getClass().getSimpleName(),
                              monitorInterval,
                              idleThreshold);
        setupMonitor();
    }
    /**
     * Stop object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              getClass().getSimpleName());
        if(activePoolConnectionHistogramName != null) {
            try {
                metricService.getMetrics().remove(activePoolConnectionHistogramName);
            } catch(Exception ignored) {
            } finally {
                activePoolConnectionHistogramName = null;
                activePoolConnectionHistogram = null;
            }
        }
        if(activePoolConnectionGaugeName != null) {
            try {
                metricService.getMetrics().remove(activePoolConnectionGaugeName);
            } catch(Exception ignored) {
            } finally {
                activePoolConnectionGaugeName = null;
            }
        }
        if(poolMonitorToken != null) {
            try {
                poolMonitorToken.cancel(true);
            } finally {
                poolMonitorToken = null;
            }
        }
    }
    /**
     * Set up the pool monitor.
     */
    private void setupMonitor()
    {
        if(dataSource == null || new DirectFieldAccessor(dataSource).getPropertyValue("pool") == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Unable to retrieve the data source");
            return;
        }
        hikariPool = (HikariPool)new DirectFieldAccessor(dataSource).getPropertyValue("pool");
        if(hikariPool == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Unable to retrieve Hikari Pool from the data source");
            return;
        }
        activePoolConnectionGaugeName = MetricRegistry.name(getClass().getSimpleName(),"activePoolConnectionsGauge");
        metricService.getMetrics().register(activePoolConnectionGaugeName,
                                            new Gauge<Integer>() {
                                                @Override
                                                public Integer getValue() {
                                                    return hikariPool.getActiveConnections();
                                                }
                                            });
        activePoolConnectionHistogramName = MetricRegistry.name(getClass().getSimpleName(),"activePoolConnectionsHistogram");
        activePoolConnectionHistogram = metricService.getMetrics().histogram(activePoolConnectionHistogramName);
        poolMonitorToken = poolMonitor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    doMonitor();
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(HikariMonitor.this,
                                          e);
                }
            }},monitorInterval,monitorInterval,TimeUnit.MILLISECONDS);
    }
    /**
     * Execute a single monitor iteration.
     */
    private void doMonitor()
    {
        int activeConnections = hikariPool.getActiveConnections();
        int totalConnections = hikariPool.getTotalConnections();
        int idleConnections = hikariPool.getIdleConnections();
        activePoolConnectionHistogram.update(activeConnections);
        Integer maxConnections = new HikariDataSourcePoolMetadata((HikariDataSource) dataSource).getMax();
        SLF4JLoggerProxy.info(this,
                              "HikariPool: active: {} idle: {} total: {} max: {}",
                              activeConnections,
                              idleConnections,
                              totalConnections,
                              maxConnections);
        double inUse = activeConnections / totalConnections;
        if(inUse > idleThreshold && notifyOnThreshold) {
            StringBuilder message = new StringBuilder();
            message.append("Warning: ").append(activeConnections).append(" active connections of ").append(totalConnections).append(" total usage ")
                .append(inUse*100).append("% exceeds the threshold of ").append(idleThreshold*100).append("%");
            notificationExecutor.notify(Notification.warn("Database Active Connection Threshold Warning",
                                                          message.toString(),
                                                          getClass().getSimpleName()));
        }
    }
    /**
     * name of the active pool gauge metric
     */
    private String activePoolConnectionGaugeName;
    /**
     * name of the active pool histogram metric
     */
    private String activePoolConnectionHistogramName;
    /**
     * active pool histogram metric
     */
    private Histogram activePoolConnectionHistogram;
    /**
     * hikari pool object
     */
    private HikariPool hikariPool;
    /**
     * provides access to metric services
     */
    @Autowired
    private MetricService metricService;
    /**
     * monitors the db pool at scheduled intervals
     */
    private final ScheduledExecutorService poolMonitor = Executors.newSingleThreadScheduledExecutor();
    /**
     * provides access to the pool monitor job
     */
    private ScheduledFuture<?> poolMonitorToken;
    /**
     * indicates whether to notify if the threshold is exceeded
     */
    @Value("${metc.hikari.pool.monitor.notify:true}")
    private boolean notifyOnThreshold;
    /**
     * interval in milliseconds to monitor the db connection pool
     */
    @Value("${metc.hikari.pool.monitor.interval.milliseconds:30000}")
    private int monitorInterval;
    /**
     * monitor threshold value
     */
    @Value("${metc.hikari.pool.monitor.threshold:0.80}")
    private double idleThreshold;
    /**
     * datasource to monitor
     */
    @Autowired
    private DataSource dataSource;
    /**
     * provides access to notification execution services
     */
    @Autowired(required=false)
    private NotificationExecutor notificationExecutor;
}
