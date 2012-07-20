package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents an object that has a version number.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: VersionedObject.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: VersionedObject.java 82384 2012-07-20 19:09:59Z colin $")
public interface VersionedObject
{
    /**
     * Get the version value.
     *
     * @return an <code>int</code> value
     */
    public int getVersion();
}
