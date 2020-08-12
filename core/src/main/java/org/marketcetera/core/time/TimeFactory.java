package org.marketcetera.core.time;

import java.time.LocalDateTime;

import org.marketcetera.core.Factory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates time representation objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface TimeFactory
        extends Factory<LocalDateTime>
{
    /**
     * Creates a time value from the given input.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>LocalDateTime</code> value
     * @throws IllegalArgumentException if the value cannot be parsed
     */
    public LocalDateTime create(String inValue);
    /**
     * Creates a time value from the given input.
     *
     * @param inValue a <code>long</code> value
     * @return a <code>LocalDateTime</code> value
     */
    public LocalDateTime create(long inValue);
}
