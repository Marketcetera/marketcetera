package org.marketcetera.core;

/* $License$ */

/**
 * Indicates that the implementor has a universal unique id.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasUuid
{
    /**
     * Get the uuid value.
     *
     * @return a <code>String</code> value
     */
    String getUuid();
}
