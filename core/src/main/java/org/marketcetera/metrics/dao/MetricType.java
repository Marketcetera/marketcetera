package org.marketcetera.metrics.dao;

/* $License$ */

/**
 * Describes the type of a {@link PersistentMetric}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum MetricType
{
    HISTOGRAM,
    COUNTER,
    TIMER,
    METER;
}
