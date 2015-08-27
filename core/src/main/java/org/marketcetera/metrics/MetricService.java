package org.marketcetera.metrics;

import com.codahale.metrics.MetricRegistry;

/* $License$ */

/**
 *
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
     * 
     *
     *
     * @return
     */
    public static MetricService getInstance()
    {
        return instance;
    }
    /**
     * 
     *
     *
     * @return
     */
    public MetricRegistry getMetrics()
    {
        return metrics;
    }
    /**
     * 
     */
    private static MetricService instance = new MetricService();;
    /**
     * 
     */
    private final MetricRegistry metrics;
}
