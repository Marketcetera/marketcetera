package org.marketcetera.metrics;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.metrics.dao.PersistentMetricDao;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Logs collected metrics to DB.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class MetricServiceDbReporter
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        register();
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        if(reporter != null) {
            reporter.close();
            reporter = null;
        }
    }
    /**
     * Get the reportInterval value.
     *
     * @return an <code>int</code> value
     */
    public int getReportInterval()
    {
        return reportInterval;
    }
    /**
     * Sets the reportInterval value.
     *
     * @param inReportInterval an <code>int</code> value
     */
    public void setReportInterval(int inReportInterval)
    {
        reportInterval = inReportInterval;
    }
    /**
     * Registers the reporter.
     */
    private void register()
    {
        SLF4JLoggerProxy.info(this,
                              "Registering metrics log reporter"); //$NON-NLS-1$
        reporter = MetricDbReporter.forRegistry(metricService.getMetrics()).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
        reporter.setMetricDao(metricDao);
        reporter.start(reportInterval,
                       TimeUnit.SECONDS);
    }
    /**
     * registered reporter
     */
    private MetricDbReporter reporter;
    /**
     * number of seconds at which to report
     */
    private int reportInterval = 30;
    /**
     * metric service
     */
    @Autowired
    private MetricService metricService;
    /**
     * provides data store access to {@link PersistentMetric} objects
     */
    @Autowired
    private PersistentMetricDao metricDao;
}
