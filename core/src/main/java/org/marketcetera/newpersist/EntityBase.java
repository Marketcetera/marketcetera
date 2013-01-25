package org.marketcetera.newpersist;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.marketcetera.core.ClassVersion;

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
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@ClassVersion("$Id: EntityBase.java 16455 2013-01-17 05:17:00Z colin $")
public abstract class EntityBase
        implements SummaryEntityBase
{
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId()
    {
        return id;
    }
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
     * Get the updated value.
     *
     * @return a <code>Date</code> value
     */
    public Date getUpdated()
    {
        return updated;
    }
    /**
     * Get the created value.
     *
     * @return a <code>Date</code> value
     */
    public Date getCreated()
    {
        return created;
    }
    /**
     * Executed before record update.
     */
    @PreUpdate
    protected void beforeUpdate()
    {
        updated = new Date();
    }
    /**
     * Executed before record create.
     */
    @PrePersist
    protected void beforeCreate()
    {
        created = new Date();
        updated = new Date();
    }
    public String toString() {
        return new ToStringBuilder(this).append(id).append(version).append(updated).append(created).toString();
    }
    /**
     * attribute name of the updated column
     */
    protected static final String ATTRIBUTE_UPDATED = "updated"; //$NON-NLS-1$
    /**
     * attribute name of the created column
     */
    protected static final String ATTRIBUTE_CREATED = "created"; //$NON-NLS-1$
    /**
     * attribute name of the id column
     */
    protected static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
    /**
     * attribute name of the version column
     */
    protected static final String ATTRIBUTE_VERSION = "version"; //$NON-NLS-1$
    /**
     * unique identifier for this object
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name=ATTRIBUTE_ID)
    private long id;
    /**
     * update counter indicates the number of times this object has been updated
     */
    @Version
    @Column(name=ATTRIBUTE_VERSION)
    private int version;
    /**
     * indicates the last time this object was updated
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name=ATTRIBUTE_UPDATED)
    private Date updated;
    /**
     * indicates the time this object was created
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name=ATTRIBUTE_CREATED)
    private Date created;
    private static final long serialVersionUID = -3037569609427708409L;
}
