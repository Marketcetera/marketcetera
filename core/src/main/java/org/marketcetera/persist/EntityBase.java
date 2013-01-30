package org.marketcetera.persist;
import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.marketcetera.core.ClassVersion;
import org.springframework.data.jpa.domain.AbstractAuditable;

/* $License$ */
/**
 * Base Class for all persistent entities.
 * 
 * <p>Provides the following attributes that every persistent entity should have.
 * <ul>
 * <li>id: The unique ID for the entity</li>
 * <li>updateCount: The count to prevent dirty
 * writes to the entity</li>
 * <li>lastUpdated: The timestamp of the last date/time
 * the entity was updated. This value is updated every time
 * the entity is updated</li> 
 * </ul>
 * <p>
 * The Entity subclass can optionally define a method that
 * returns a customized name to use for the entity in user visible messages.
 * The method needs to have the following signature.
 * <pre>
 * private static {@link org.marketcetera.util.log.I18NMessage0P getUserFriendlyName()}
 * </pre>
 * If this method is not defined, the {@link Class#getSimpleName() simple}
 * class name is used to refer to the entity in user visible messages.
 * 
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlants</a>
 */
@MappedSuperclass
@NotThreadSafe
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public abstract class EntityBase
        extends AbstractAuditable<User,Long>
{
    /**
     * Get the version value.
     *
     * @return an <code>int</code> value
     */
    public int getVersion()
    {
        return version;
    }
    /**
     * Create a new EntityBase instance.
     */
    protected EntityBase() {}
    /**
     * update counter indicates the number of times this object has been updated
     */
    @Version
    @XmlAttribute(required=true)
    @Column(name="version")
    private int version;
    private static final long serialVersionUID = -3037569609427708409L;
}
