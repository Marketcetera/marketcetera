package org.marketcetera.dao.impl;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.marketcetera.systemmodel.VersionedObject;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for versioned system objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@MappedSuperclass
@ClassVersion("$Id$")
public abstract class PersistentVersionedObject
        extends PersistentSystemObject
        implements VersionedObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.VersionedObject#getVersion()
     */
    @Override
    public final int getVersion()
    {
        return version;
    }
    /**
     * object version value
     */
    @Version
    private int version;
}
