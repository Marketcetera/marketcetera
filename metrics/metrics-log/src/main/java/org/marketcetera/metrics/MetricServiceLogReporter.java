package org.marketcetera.metrics;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.Slf4jReporter;

/* $License$ */

/**
 * Logs collected metrics to SLF4J.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class MetricServiceLogReporter
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
        reporter = Slf4jReporter.forRegistry(metricService.getMetrics()).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).outputTo(LoggerFactory.getLogger(category)).build();
        reporter.start(reportInterval,
                       TimeUnit.SECONDS);
    }
    /**
     * registered reporter
     */
    private Slf4jReporter reporter;
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
     * logging category used to report metrics (at info level)
     */
    public static final String category = "metrics"; //$NON-NLS-1$
}
