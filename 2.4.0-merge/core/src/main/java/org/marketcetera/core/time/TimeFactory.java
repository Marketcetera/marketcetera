package org.marketcetera.core.time;

import org.joda.time.DateTime;
import org.marketcetera.core.Factory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates time representation objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TimeFactory.java 83355 2013-06-26 18:21:12Z colin $
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface TimeFactory
        extends Factory<DateTime>
{
    /**
     * Creates a time value from the given input.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>DateTime</code> value
     * @throws IllegalArgumentException if the value cannot be parsed
     */
    public DateTime create(String inValue);
    /**
     * Creates a time value from the given input.
     *
     * @param inValue a <code>long</code> value
     * @return a <code>DateTime</code> value
     */
    public DateTime create(long inValue);
}
