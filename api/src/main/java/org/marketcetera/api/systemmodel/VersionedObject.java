package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Represents an object that has a version number.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: VersionedObject.java 82316 2012-03-21 21:13:27Z colin $
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
