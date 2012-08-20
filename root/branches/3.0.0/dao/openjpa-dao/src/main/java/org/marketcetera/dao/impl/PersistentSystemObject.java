package org.marketcetera.dao.impl;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.marketcetera.api.systemmodel.SystemObject;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Base class for persistent system objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentSystemObject.java 82307 2012-03-02 03:13:45Z colin $
 * @since $Release$
 */
@MappedSuperclass
@ClassVersion("$Id: PersistentSystemObject.java 82307 2012-03-02 03:13:45Z colin $")
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
