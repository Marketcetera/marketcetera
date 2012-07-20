package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents an object that has a version number.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface VersionedObject
{
    /**
     * Get the version value.
     *
     * @return an <code>int</code> value
     */
    public int getVersion();
}
