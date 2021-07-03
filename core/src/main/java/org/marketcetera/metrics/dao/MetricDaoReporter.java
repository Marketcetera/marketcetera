package org.marketcetera.metrics.dao;

import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetricDaoReporter
        extends ScheduledReporter
{
    /**
     * Create a new MetricDaoReporter instance.
     *
     * @param inRegistry
     * @param inName
     * @param inFilter
     * @param inRateUnit
     * @param inDurationUnit
     */
    public MetricDaoReporter(MetricRegistry inRegistry,
                             String inName,
                             MetricFilter inFilter,
                             TimeUnit inRateUnit,
                             TimeUnit inDurationUnit)
    {
        super(inRegistry,
              inName,
              inFilter,
              inRateUnit,
              inDurationUnit);
    }
    /**
     * Create a new MetricDaoReporter instance.
     *
     * @param inRegistry
     * @param inName
     * @param inFilter
     * @param inRateUnit
     * @param inDurationUnit
     * @param inExecutor
     * @param inShutdownExecutorOnStop
     * @param inDisabledMetricAttributes
     */
    public MetricDaoReporter(MetricRegistry inRegistry,
                             String inName,
                             MetricFilter inFilter,
                             TimeUnit inRateUnit,
                             TimeUnit inDurationUnit,
                             ScheduledExecutorService inExecutor,
                             boolean inShutdownExecutorOnStop,
                             Set<MetricAttribute> inDisabledMetricAttributes)
    {
        super(inRegistry,
              inName,
              inFilter,
              inRateUnit,
              inDurationUnit,
              inExecutor,
              inShutdownExecutorOnStop,
              inDisabledMetricAttributes);
    }
    /**
     * Create a new MetricDaoReporter instance.
     *
     * @param inRegistry
     * @param inName
     * @param inFilter
     * @param inRateUnit
     * @param inDurationUnit
     * @param inExecutor
     * @param inShutdownExecutorOnStop
     */
    public MetricDaoReporter(MetricRegistry inRegistry,
                             String inName,
                             MetricFilter inFilter,
                             TimeUnit inRateUnit,
                             TimeUnit inDurationUnit,
                             ScheduledExecutorService inExecutor,
                             boolean inShutdownExecutorOnStop)
    {
        super(inRegistry,
              inName,
              inFilter,
              inRateUnit,
              inDurationUnit,
              inExecutor,
              inShutdownExecutorOnStop);
    }
    /**
     * Create a new MetricDaoReporter instance.
     *
     * @param inRegistry
     * @param inName
     * @param inFilter
     * @param inRateUnit
     * @param inDurationUnit
     * @param inExecutor
     */
    public MetricDaoReporter(MetricRegistry inRegistry,
                             String inName,
                             MetricFilter inFilter,
                             TimeUnit inRateUnit,
                             TimeUnit inDurationUnit,
                             ScheduledExecutorService inExecutor)
    {
        super(inRegistry,
              inName,
              inFilter,
              inRateUnit,
              inDurationUnit,
              inExecutor);
    }
    /* (non-Javadoc)
     * @see com.codahale.metrics.ScheduledReporter#report(java.util.SortedMap, java.util.SortedMap, java.util.SortedMap, java.util.SortedMap, java.util.SortedMap)
     */
    @Override
    public void report(SortedMap<String,Gauge> inGauges,
                       SortedMap<String,Counter> inCounters,
                       SortedMap<String,Histogram> inHistograms,
                       SortedMap<String,Meter> inMeters,
                       SortedMap<String,Timer> inTimers)
    {
        throw new UnsupportedOperationException(); // TODO
    }
}
