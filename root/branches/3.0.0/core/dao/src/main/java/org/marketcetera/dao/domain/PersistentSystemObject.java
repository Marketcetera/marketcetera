package org.marketcetera.dao.domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 * Base class for persistent system objects.
 *
 * @version $Id$
 * @since $Release$
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
public abstract class PersistentSystemObject
        implements SystemObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.SystemObject#getId()
     */
    @Override
    @XmlAttribute
    public long getId()
    {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PersistentSystemObject)) {
            return false;
        }
        PersistentSystemObject other = (PersistentSystemObject) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    protected PersistentSystemObject() {}
    protected PersistentSystemObject(SystemObject inSystemObject)
    {
        id = inSystemObject.getId();
    }
    /**
     * unique identifier for this object
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private volatile long id;
    private static final long serialVersionUID = 1L;
}
