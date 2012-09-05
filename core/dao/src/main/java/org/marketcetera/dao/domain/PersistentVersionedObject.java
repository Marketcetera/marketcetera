package org.marketcetera.dao.domain;

import javax.persistence.*;

import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Base class for versioned system objects.
 *
 * @version $Id: PersistentVersionedObject.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@MappedSuperclass
@Access(AccessType.FIELD)
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

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * object version value
     */
//    @Column(name="version")
    @Version
    private int version;
}
