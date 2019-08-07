package org.marketcetera.core;

/* $License$ */

/**
 * Indicates that the implementation has some in-memory cache.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Cachable
{
    /**
     * Clear the cached data.
     */
    void clear();
}
