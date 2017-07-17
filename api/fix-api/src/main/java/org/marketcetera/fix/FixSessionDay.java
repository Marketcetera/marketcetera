package org.marketcetera.fix;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/* $License$ */

/**
 * Enumerates the days of the week for use in active vs non-active FIX session days.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum FixSessionDay
{
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;
    /**
     * Get the computed value for the given collection of day values.
     *
     * @param inDays a <code>Collection&lt;FixSessionDay&gt;</code> value
     * @return an <code>int</code> value
     */
    public static int getValue(Collection<FixSessionDay> inDays)
    {
        int value = 0;
        for(FixSessionDay fixSessionDay : inDays) {
            value += 1 << (fixSessionDay.ordinal()+1);
        }
        return value;
    }
    /**
     * Get the set of days corresponding to the given input value.
     *
     * @param inValue an <code>int</code> value
     * @return a <code>Set&lt;FixSessionDay&gt;</code> value
     */
    public static Set<FixSessionDay> getValuesFor(int inValue)
    {
        Set<FixSessionDay> values = new HashSet<>();
        for(FixSessionDay day : FixSessionDay.values()) {
            int derivedValue = 1 << (day.ordinal()+1);
            if((inValue & derivedValue) == derivedValue) {
                values.add(day);
            }
        }
        return values;
    }
    /**
     * Indicate if the session should be active today using the given computed value.
     *
     * @param inValue an <code>int</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isActiveToday(int inValue)
    {
        if(inValue == 0) {
            return true;
        }
        int testValue = 1 << (ordinal()+1);
        return (testValue & inValue) == testValue;
    }
}
