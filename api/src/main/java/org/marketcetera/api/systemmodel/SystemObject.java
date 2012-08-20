package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Represents an object with a unique identifier.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemObject.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
public interface SystemObject
{
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId();
}
