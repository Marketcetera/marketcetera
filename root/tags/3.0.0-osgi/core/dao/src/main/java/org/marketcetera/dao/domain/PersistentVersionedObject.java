package org.marketcetera.dao.domain;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Base class for versioned system objects.
 *
 * @version $Id$
 * @since $Release$
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
public abstract class PersistentVersionedObject
        extends PersistentSystemObject
        implements VersionedObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.VersionedObject#getVersion()
     */
    @Override
    @XmlAttribute
    public final int getVersion()
    {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    /**
     * Create a new PersistentVersionedObject instance.
     */
    protected PersistentVersionedObject() {}
    /**
     * Create a new PersistentVersionedObject instance.
     *
     * @param inVersionedObject a <code>VersionedObject</code> value
     */
    protected PersistentVersionedObject(VersionedObject inVersionedObject)
    {
        super(inVersionedObject);
        version = inVersionedObject.getVersion();
    }
    /**
     * object version value
     */
    @Version
    private int version;
}
