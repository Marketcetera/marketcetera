package org.marketcetera.trade;

import java.util.Collection;

/* $License$ */

/**
 * Provides option services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OptionService
{
    /**
     * Gets the underlying symbol, given the option root symbol.
     *
     * @param inOptionRoot the option root symbol.
     * @return the underlying symbol, if a mapping is found. null otherwise.
     */
    String getUnderlying(String inOptionRoot);
    /**
     * Returns the collection of option roots for the underlying symbol.
     *
     * @param inUnderlying the underlying symbol.
     * @return the sorted collection of option roots for the underlying symbol.
     * If no mapping is found, a null value is returned. The returned collection
     * is not modifiable.
     */
    Collection<String> getOptionRoots(String inUnderlying);
}
