package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents an object with a unique identifier.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemObject.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: SystemObject.java 82316 2012-03-21 21:13:27Z colin $")
public interface SystemObject
{
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId();
}
