package org.marketcetera.core.systemmodel;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Represents an object that has a version number.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: VersionedObject.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: VersionedObject.java 82316 2012-03-21 21:13:27Z colin $")
public interface VersionedObject
{
    /**
     * Get the version value.
     *
     * @return an <code>int</code> value
     */
    public int getVersion();
}
