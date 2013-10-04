package org.marketcetera.persist;
import java.util.Date;

import javax.persistence.*;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Base Class for all persistent entities.
 * Provides the following attributes that every
 * persistent entity should have.
 *
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
 */
@MappedSuperclass
@ClassVersion("$Id$")
public abstract class EntityBase
        implements SummaryEntityBase
{
    /**
     * The Entity ID. This ID uniquely represents an instance
     * of this entity type in the system. The entity ID is
     * generated automatically by the ORM system.
     * 
     * @return The entity ID.
     */
    public long getId()
    {
        return id;
    }
    /**
     * Sets the Entity ID. The Entity ID is inititalized
     * by the system, the first time an entity is saved.
     * This method should not be invoked, its automatically
     * invoked when the entity is saved for the first time.
     *
     * @param id The Entity ID.
     */
    protected void setId(long id)
    {
        this.id = id;
    }
    /**
     * An Update Count of the number of times this object
     * has been updated. This field is used to prevent dirty
     * updates to this object.
     * This field should not be set to any arbitrary value.
     * Specifically this field should only be set to a value
     * that has been obtained from the {@link #getUpdateCount()}.
     * 
     * @return The update count of the object
     */
    public int getUpdateCount()
    {
        return updateCount;
    }
    /**
     * Sets the update count of this object. Do not
     * set this attribute to any arbitrary value. See
     * {@link #getUpdateCount()} for more details
     *
     * @param updateCount The update count for this object
     *
     * @see #getUpdateCount()
     */
    protected void setUpdateCount(int updateCount)
    {
        this.updateCount = updateCount;
    }
    /**
     * The time last time this object was updated
     *
     * @return  time the object was last modified.
     */
    public Date getLastUpdated()
    {
        return lastUpdated;
    }
    public String toString() {
        return "EntityBase{" + //$NON-NLS-1$
                "id=" + id + //$NON-NLS-1$
                ", updateCount=" + updateCount + //$NON-NLS-1$
                ", lastUpdated=" + lastUpdated + //$NON-NLS-1$
                ", identity=" + System.identityHashCode(this) + //$NON-NLS-1$
                '}';
    }
    /**
     * 
     */
    @Id
    @GeneratedValue
    private long id = UNINITIALIZED;
    /**
     * 
     */
    @Version
    private int updateCount = UNINITIALIZED;
    /**
     * 
     */
    @Temporal(TemporalType.TIMESTAMP) //i18n_datetime when storing to / retrieving from database
    private Date lastUpdated;
    /**
     * 
     */
    protected static final int UNINITIALIZED = -1;
    private static final long serialVersionUID = -7445081112376896281L;
}
