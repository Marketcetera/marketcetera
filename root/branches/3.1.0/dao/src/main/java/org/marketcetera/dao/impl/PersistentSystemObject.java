package org.marketcetera.dao.impl;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.marketcetera.systemmodel.SystemObject;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for persistent system objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@MappedSuperclass
@ClassVersion("$Id$")
public abstract class PersistentSystemObject
        implements SystemObject, Comparable<PersistentSystemObject>
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.SystemObject#getId()
     */
    @Override
    public long getId()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(PersistentSystemObject inOther)
    {
        if(inOther == null) {
            return 1;
        }
        return new Long(id).compareTo(inOther.getId());
    }
    /**
     * unique identifier for this object
     */
    @GeneratedValue
    @Id
    private volatile long id;
}
