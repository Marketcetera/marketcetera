package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Represents an object that has a version number.
 *
 * @version $Id$
 * @since $Release$
 */
public interface VersionedObject
{
    /**
     * Get the version value.
     *
     * @return an <code>int</code> value
     */
    public int getVersion();
}
