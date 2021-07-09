package org.marketcetera.metrics;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.marketcetera.metrics.dao.MetricType;
import org.marketcetera.metrics.dao.PersistentMetric;
import org.marketcetera.metrics.dao.PersistentMetricDao;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Writes metrics to the DB.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetricDbReporter
        extends ScheduledReporter
{
    /**
     * Returns a new {@link Builder} for {@link MetricDbReporter}.
     *
     * @param inRegistry the registry to report
     * @return a {@link Builder} instance for a {@link MetricDbReporter}
     */
    public static Builder forRegistry(MetricRegistry inRegistry)
    {
        return new Builder(inRegistry);
    }
    /* (non-Javadoc)
     * @see com.codahale.metrics.ScheduledReporter#report(java.util.SortedMap, java.util.SortedMap, java.util.SortedMap, java.util.SortedMap, java.util.SortedMap)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void report(SortedMap<String,Gauge> inGauges,
                       SortedMap<String,Counter> inCounters,
                       SortedMap<String,Histogram> inHistograms,
                       SortedMap<String,Meter> inMeters,
                       SortedMap<String,Timer> inTimers)
    {
        Date timestamp = new Date();
        Collection<PersistentMetric> dbMetrics = Lists.newArrayList();
        for(Map.Entry<String,Gauge> entry : inGauges.entrySet()) {
            try {
                PersistentMetric metric = new PersistentMetric();
                metric.setType(MetricType.GAUGE);
                Gauge gauge = entry.getValue();
                setCommonFields(entry.getKey(),
                                timestamp,
                                metric);
                metric.setValue(String.valueOf(gauge.getValue()));
                dbMetrics.add(metric);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        for(Map.Entry<String,Counter> entry : inCounters.entrySet()) {
            try {
                PersistentMetric metric = new PersistentMetric();
                Counter counter = entry.getValue();
                metric.setType(MetricType.COUNTER);
                setCommonFields(entry.getKey(),
                                timestamp,
                                metric);
                metric.setCount(counter.getCount());
                dbMetrics.add(metric);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        for(Map.Entry<String,Histogram> entry : inHistograms.entrySet()) {
            try {
                PersistentMetric metric = new PersistentMetric();
                Histogram histogram = entry.getValue();
                metric.setType(MetricType.HISTOGRAM);
                setCommonFields(entry.getKey(),
                                timestamp,
                                metric);
                metric.setCount(histogram.getCount());
                processSnapshot(histogram.getSnapshot(),
                                metric);
                dbMetrics.add(metric);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        for(Map.Entry<String,Meter> entry : inMeters.entrySet()) {
            try {
                PersistentMetric metric = new PersistentMetric();
                Meter meter = entry.getValue();
                metric.setType(MetricType.METER);
                setCommonFields(entry.getKey(),
                                timestamp,
                                metric);
                metric.setCount(meter.getCount());
                metric.setM1(new BigDecimal(meter.getOneMinuteRate()));
                metric.setM15(new BigDecimal(meter.getFifteenMinuteRate()));
                metric.setM5(new BigDecimal(meter.getFiveMinuteRate()));
                metric.setMeanRate(new BigDecimal(meter.getMeanRate()));
                dbMetrics.add(metric);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        for(Map.Entry<String,Timer> entry : inTimers.entrySet()) {
            try {
                PersistentMetric metric = new PersistentMetric();
                Timer timer = entry.getValue();
                metric.setType(MetricType.TIMER);
                setCommonFields(entry.getKey(),
                                timestamp,
                                metric);
                metric.setCount(timer.getCount());
                metric.setM1(new BigDecimal(timer.getOneMinuteRate()));
                metric.setM15(new BigDecimal(timer.getFifteenMinuteRate()));
                metric.setM5(new BigDecimal(timer.getFiveMinuteRate()));
                metric.setMeanRate(new BigDecimal(timer.getMeanRate()));
                processSnapshot(timer.getSnapshot(),
                                metric);
                dbMetrics.add(metric);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        metricDao.saveAll(dbMetrics);
    }
    /**
     * Sets the metricDao value.
     *
     * @param inMetricDao a <code>PersistentMetricDao</code> value
     */
    public void setMetricDao(PersistentMetricDao inMetricDao)
    {
        metricDao = inMetricDao;
    }
    /**
     * Update the given metric with the given snapshot.
     *
     * @param inSnapshot a <code>Snapshot</code> value
     * @param inOutMetric a <code>PersistentMetric</code> value
     */
    private void processSnapshot(Snapshot inSnapshot,
                                 PersistentMetric inOutMetric)
    {
        inOutMetric.setP75(new BigDecimal(inSnapshot.get75thPercentile()));
        inOutMetric.setP95(new BigDecimal(inSnapshot.get95thPercentile()));
        inOutMetric.setP98(new BigDecimal(inSnapshot.get98thPercentile()));
        inOutMetric.setP999(new BigDecimal(inSnapshot.get999thPercentile()));
        inOutMetric.setP99(new BigDecimal(inSnapshot.get99thPercentile()));
        inOutMetric.setMax(new BigDecimal(inSnapshot.getMax()));
        inOutMetric.setMean(new BigDecimal(inSnapshot.getMean()));
        inOutMetric.setMedian(new BigDecimal(inSnapshot.getMedian()));
        inOutMetric.setMin(new BigDecimal(inSnapshot.getMin()));
        inOutMetric.setStdDev(new BigDecimal(inSnapshot.getStdDev()));
    }
    /**
     * Set the common fields for the given metric.
     *
     * @param inName a <code>String</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inOutMetric a <code>PersistentMetric</code> value
     */
    private void setCommonFields(String inName,
                                 Date inTimestamp,
                                 PersistentMetric inOutMetric)
    {
        inOutMetric.setName(inName);
        inOutMetric.setTimestamp(inTimestamp);
        inOutMetric.setDurationUnit(finalDurationUnit.name());
        inOutMetric.setRateUnit(finalRateUnit.name());
        DateTime timestamp = new DateTime(inTimestamp);
        inOutMetric.setHour(timestamp.getHourOfDay());
        inOutMetric.setMinute(timestamp.getMinuteOfHour());
        inOutMetric.setSecond(timestamp.getSecondOfMinute());
        inOutMetric.setMillis(timestamp.getMillisOfSecond());
    }
    /**
     * Create a new MetricDbReporter instance.
     *
     * @param inRegistry a <code>MetricRegistry</code> value
     * @param inName a <code>String</code> value
     * @param inFilter a <code>MetricFilter</code> value
     * @param inRateUnit a <code>TimeUnit</code> value
     * @param inDurationUnit a <code>TimeUnit</code> value
     * @param inExecutor a <code>ScheduledExecutorService</code> value
     * @param inShutdownExecutorOnStop a <code>boolean</code> value
     * @param inDisabledMetricAttributes a <code>Set&lt;MetricAttribute&gt;</code> value
     */
    private MetricDbReporter(MetricRegistry inRegistry,
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
     * Builder for {@link MetricDbReporter} instances.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class Builder
    {
        /**
         * Create a new Builder instance.
         *
         * @param inRegistry a <code>MetricRegistry</code> value
         */
        private Builder(MetricRegistry inRegistry)
        {
            registry = inRegistry;
            prefix = "";
            rateUnit = TimeUnit.SECONDS;
            durationUnit = TimeUnit.MILLISECONDS;
            filter = MetricFilter.ALL;
            executor = null;
            shutdownExecutorOnStop = true;
            disabledMetricAttributes = Collections.emptySet();
        }
        /**
         * Specifies whether or not, the executor (used for reporting) will be stopped with same time with reporter.
         * Default value is true.
         * Setting this parameter to false, has the sense in combining with providing external managed executor via {@link #scheduleOn(ScheduledExecutorService)}.
         *
         * @param inShutdownExecutorOnStop if true, then executor will be stopped in same time with this reporter
         * @return {@code this}
         */
        public Builder shutdownExecutorOnStop(boolean inShutdownExecutorOnStop)
        {
            shutdownExecutorOnStop = inShutdownExecutorOnStop;
            return this;
        }
        /**
         * Specifies the executor to use while scheduling reporting of metrics.
         * Default value is null.
         * Null value leads to executor will be auto created on start.
         *
         * @param inExecutor the executor to use while scheduling reporting of metrics.
         * @return {@code this}
         */
        public Builder scheduleOn(ScheduledExecutorService inExecutor) {
            executor = inExecutor;
            return this;
        }
        /**
         * Prefix all metric names with the given string.
         *
         * @param inPrefix the prefix for all metric names
         * @return {@code this}
         */
        public Builder prefixedWith(String inPrefix) {
            prefix = inPrefix;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param inRateUnit a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit inRateUnit) {
            rateUnit = inRateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param inDurationUnit a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit inDurationUnit) {
            durationUnit = inDurationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param inFilter a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter inFilter) {
            filter = inFilter;
            return this;
        }
        /**
         * Don't report the passed metric attributes for all metrics (e.g. "p999", "stddev" or "m15").
         * See {@link MetricAttribute}.
         *
         * @param inDisabledMetricAttributes a set of {@link MetricAttribute}
         * @return {@code this}
         */
        public Builder disabledMetricAttributes(Set<MetricAttribute> inDisabledMetricAttributes) {
            disabledMetricAttributes = inDisabledMetricAttributes;
            return this;
        }

        /**
         * Builds a {@link MetricDbReporter} with the given properties.
         *
         * @return a {@link MetricDbReporter}
         */
        public MetricDbReporter build() {
            finalDurationUnit = durationUnit;
            finalRateUnit = rateUnit;
            return new MetricDbReporter(registry,
                                        prefix,
                                        filter,
                                        rateUnit,
                                        durationUnit,
                                        executor,
                                        shutdownExecutorOnStop,
                                        disabledMetricAttributes);
        }
        private final MetricRegistry registry;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;
    }
    /**
     * holds the rate unit selected by the builder
     */
    private static TimeUnit finalRateUnit;
    /**
     * holds the duration unit selected by the builder
     */
    private static TimeUnit finalDurationUnit;
    /**
     * provides data store access to metrics
     */
    private PersistentMetricDao metricDao;
}
