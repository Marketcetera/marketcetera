package org.marketcetera.core;

/* $License$ */

/**
 * Indicates that the implementation has some in-memory cache.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Cacheable
{
    /**
     * Clear the cached data.
     */
    void clear();
}
