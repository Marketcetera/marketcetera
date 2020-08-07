package org.marketcetera.metrics;

import org.springframework.stereotype.Service;

import com.codahale.metrics.MetricRegistry;

/* $License$ */

/**
 * Provides metric services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class MetricService
{
    /**
     * Create a new MetricService instance.
     */
    public MetricService()
    {
        metrics = new MetricRegistry();
        instance = this;
    }
    /**
     * Get the singleton instance of the metric service.
     *
     * @return a <code>MetricService</code> value
     */
    public static MetricService getInstance()
    {
        return instance;
    }
    /**
     * Get the underlying MetricRegistry value.
     *
     * @return a <code>MetricRegistry</code> value
     */
    public MetricRegistry getMetrics()
    {
        return metrics;
    }
    /**
     * singleton metrics instance
     */
    private static MetricService instance;
    /**
     * metrics registry
     */
    private final MetricRegistry metrics;
}
