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
}
