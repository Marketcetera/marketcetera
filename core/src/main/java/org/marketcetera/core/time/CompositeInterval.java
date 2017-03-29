package org.marketcetera.core.time;

import java.util.Set;

import org.joda.time.DateTime;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Represents one or more intervals in a cohesive collection.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class CompositeInterval
{
    /**
     * Get the intervals value.
     *
     * @return a <code>Set&lt;Interval&gt;</code> value
     */
    public Set<Interval> getIntervals()
    {
        return intervals;
    }
    /**
     * Set the intervals value.
     *
     * @param inIntervals a <code>Set&lt;Interval&gt;</code> value
     */
    public void setIntervals(Set<Interval> inIntervals)
    {
        intervals = inIntervals;
    }
    /**
     * Indicate if the composite interval contains the given point in time.
     *
     * @param inTime a <code>DateTime</code> value
     * @return a <code>boolean</code> value
     */
    public boolean contains(DateTime inTime)
    {
        for(Interval interval : intervals) {
            SLF4JLoggerProxy.debug(this,
                                   "Checking to see if {} contains {}",
                                   interval,
                                   inTime);
            if(interval.contains(inTime)) {
                return true;
            }
        }
        return false;
    }
    /**
     * intervals of the composite interval
     */
    private Set<Interval> intervals = Sets.newHashSet();
}
