package org.marketcetera.metrics;

import com.codahale.metrics.MetricRegistry;

/* $License$ */

/**
 * Provides services related to collecting and calculating metric.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetricService
{
    /**
     * Create a new MetricService instance.
     */
    public MetricService()
    {
        metrics = new MetricRegistry();
    }
    /**
     * Gets the metric service instance.
     *
     * @return a <code>MetricService</code> value
     */
    public static MetricService getInstance()
    {
        return instance;
    }
    /**
     * Gets the metrics object.
     *
     * @return a <code>MetricRegistry</code> value
     */
    public MetricRegistry getMetrics()
    {
        return metrics;
    }
    /**
     * metric service instance
     */
    private static MetricService instance = new MetricService();;
    /**
     * metrics core instance
     */
    private final MetricRegistry metrics;
}
