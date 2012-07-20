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
        implements SystemObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.SystemObject#getId()
     */
    @Override
    public long getId()
    {
        return id;
    }
    /**
     * unique identifier for this object
     */
    @GeneratedValue
    @Id
    private volatile long id;
}
