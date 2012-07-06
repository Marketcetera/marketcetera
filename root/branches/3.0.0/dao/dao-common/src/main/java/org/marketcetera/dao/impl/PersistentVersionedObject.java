package org.marketcetera.dao.impl;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.marketcetera.core.systemmodel.VersionedObject;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Base class for versioned system objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentVersionedObject.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@MappedSuperclass
@ClassVersion("$Id: PersistentVersionedObject.java 82316 2012-03-21 21:13:27Z colin $")
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
