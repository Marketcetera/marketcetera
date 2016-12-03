package org.marketcetera.core.time;

import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.collect.Sets;

/* $License$ */

/**
 *
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
     * Sets the intervals value.
     *
     * @param inIntervals a <code>Set&lt;Interval&gt;</code> value
     */
    public void setIntervals(Set<Interval> inIntervals)
    {
        intervals = inIntervals;
    }
    public boolean contains(DateTime inTime)
    {
        for(Interval interval : intervals) {
            if(interval.contains(inTime)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 
     */
    private Set<Interval> intervals = Sets.newHashSet();
}
