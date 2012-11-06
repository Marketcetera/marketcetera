package org.marketcetera.api.systemmodel;

import java.io.Serializable;

/* $License$ */

/**
 * Represents an object with a unique identifier.
 *
 * @version $Id$
 * @since $Release$
 */
public interface SystemObject
        extends Serializable
{
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId();
}
