package org.marketcetera.dao.domain;

import javax.persistence.*;

import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 * Base class for persistent system objects.
 *
 * @version $Id: PersistentSystemObject.java 82307 2012-03-02 03:13:45Z colin $
 * @since $Release$
 */
@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class PersistentSystemObject
        implements SystemObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.SystemObject#getId()
     */
    @Override
    @GeneratedValue
    @Id
    public long getId()
    {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * unique identifier for this object
     */
    private volatile long id;
}
