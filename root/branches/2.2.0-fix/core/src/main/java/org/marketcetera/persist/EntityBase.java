package org.marketcetera.persist;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javax.persistence.*;
import java.util.Date;

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
@ClassVersion("$Id$") //$NON-NLS-1$
@MappedSuperclass
public abstract class EntityBase implements SummaryEntityBase {
    private static final long serialVersionUID = -7445081112376896281L;

    /* *******************Remote Operations****************** */

    
    /**
     * This method is invoked by a client facing API to save the entity.
     * Invoking this method, will marshall this object to the server-side
     * and invoke {@link #saveLocal(EntityManager, PersistContext)} on it.
     *  
     * @param context persist context instance if needed.
     * 
     * @throws EntityExistsException if the entity being saved already exists
     * @throws OptimisticLockException if the entity being saved is stale,
     * ie. it doesn't represent the most recent version of that entity in
     * the database. 
     * @throws PersistenceException if there was an error saving
     * the entity
     */
    protected void saveRemote(PersistContext context) throws PersistenceException {
        SaveResult curState = createSaveResult();
        boolean opSucceeded = false;
        try {
            applyRemote(EntityRemoteServices.getInstance().save(this, context));
            opSucceeded = true;
        } finally {
            //Undo any dirty changes made to the object if it
            //was saved locally. Do note that this operation is
            //not needed when the entity is saved remotely as
            //dirty changes to entity remain at the server-side
            if(!opSucceeded) {
                applyRemote(curState);
            }
        }
    }

    /**
     * This method is invoked by a client facing API to delete the entity.
     * Invoking this method will marshall this object to the server-side
     * and invoke {@link #deleteLocal(EntityManager, PersistContext)} on it.
     * 
     * @param context the persist context if needed.
     *
     * @throws PersistenceException if there was an error deleting
     * the entity
     */
    protected void deleteRemote(PersistContext context) throws PersistenceException {
        applyRemote(EntityRemoteServices.getInstance().
            delete(this, context));
    }

    /**
     * Applies the save result contents to this instance. This method is
     * invoked on the client-side, to apply state changes caused by
     * persistence, to the entity being saved or deleted.
     *
     * @param saveResult the save result as returned by
     * {@link #saveLocal(javax.persistence.EntityManager, PersistContext)}
     * invocation on the server-side.
     */
    protected void applyRemote(SaveResult saveResult) {
        SLF4JLoggerProxy.debug(this,"Applying SaveResult {}", saveResult); //$NON-NLS-1$
        setId(saveResult.getId());
        setUpdateCount(saveResult.getUpdateCount());
        setLastUpdated(saveResult.getTimestamp());
    }

    /**
     * Runs the supplied transaction and returns the results.
     * This method is available as an escape hatch to run operations
     * that need persistence interaction but do not fall in the
     * category of <code>save()</code> or <code>delete()</code> operations.
     *
     * @param txn the transaction instance.
     * @param ctx persist context if needed
     *
     * @return results of the transaction, if any
     *
     * @throws PersistenceException if there was an error
     * executing the transaction
     */
    protected static <R> R executeRemote(Transaction<R> txn,
                                         PersistContext ctx)
            throws PersistenceException {
        return EntityRemoteServices.getInstance().execute(txn,ctx);
    }

    /* *******************Local Operations****************** */

    /**
     * Saves the entity. This method is invoked locally
     * by the persistence system when an invocation to
     * {@link #saveRemote(PersistContext)} is made.
     *
     * @param em The entity manager instance.
     * @param context The persistent context, if one was
     * supplied to {@link #saveRemote(org.marketcetera.persist.PersistContext)}
     *
     * @return the save result containing the result of
     * state changes made to the entity as a result of saving it.
     * 
     * @throws PersistenceException if there were errors saving
     * the entity.
     */
    protected final SaveResult saveLocal(EntityManager em,
                                         PersistContext context)
            throws PersistenceException {
        boolean persistent = isPersistent();
        EntityBase entity = this;
        preSaveLocal(em,context);
        if(persistent) {
            entity = em.merge(this);
            SLF4JLoggerProxy.debug(this, "Merged {}", entity); //$NON-NLS-1$
        } else {
            em.persist(entity);
            SLF4JLoggerProxy.debug(this, "Persisted {}", entity); //$NON-NLS-1$
        }
        //Let subclasses run additional operations
        postSaveLocal(em,entity,context);
        //Since entity updates are usually deferred
        //Flush the entity manager, so that we can
        //get the correct update count, timestamp values.
        em.flush();
        return entity.createSaveResult();
    }

    /**
     * This mehtod may be over-ridden by subclasses to carry out extra
     * processing prior to the entity's save to the database.
     * 
     * @param em The entity manager instance
     * @param context the persist context, if one was supplied to
     * {@link #saveRemote(org.marketcetera.persist.PersistContext)}
     *
     * @throws PersistenceException if there was an error during
     * processing.
     */
    protected void preSaveLocal(EntityManager em,
                                PersistContext context)
            throws PersistenceException {
        //do nothing, subclasses may do something here.
    }

    /**
     * This method may be over-ridden by subclasses to
     * carry out extra processing after the entity
     * has been saved. This method is invoked within the
     * context of the transaction within which the changes to
     * this entity were saved. The default implementation doesn't
     * do anything
     * <p>
     * Entities over-riding this method should use
     * the supplied <code>merged</code> entity instance instead
     * of referencing <code>this</code> for accessing or making
     * persistent changes to the entity state.
     * The <code>merged</code> entity is tracked by ORM and changes
     * to it will get persisted to the database.
     * <p>
     *
     * @param em The entity manager.
     * @param merged The merged entity monitored by the ORM tool.
     * @param context The persist context instance, if one was
     * supplied to
     * {@link #saveRemote(org.marketcetera.persist.PersistContext)}
     *
     * @throws PersistenceException if there were errors during processing
     */
    protected void postSaveLocal(EntityManager em,
                                 EntityBase merged,
                                 PersistContext context)
            throws PersistenceException {
        //do nothing, subclasses may do something here.
    }

    /**
     * Creates a save result capturing the current state of this entity.
     * Subclasses may over-ride this method to return their own subclasses
     * of SaveResult in case they have state changes that are made while
     * they are persisted, that need to be communicated back to the
     * instance on which the persistent operation like
     * <code>save()</code> / <code>delete()</code>
     * was invoked.
     * 
     * @return the save result for this entity.
     */
    protected SaveResult createSaveResult() {
        return new SaveResult(getId(),getUpdateCount(), getLastUpdated());
    }

    /**
     * This method is invoked as a result of invocation of
     * {@link #deleteRemote(PersistContext)}.
     * This method deletes the entity via the supplied
     * entity manager instance.
     *
     * @param em The entity manager for the entity.
     * @param context the persist context that was supplied
     * to {@link #deleteRemote(org.marketcetera.persist.PersistContext)}
     *
     * @return a save result that will set the invoking entity's
     * state such that it can saved again with a different identity.
     * 
     * @throws PersistenceException if there was an error saving the entity.
     */
    protected SaveResult deleteLocal(EntityManager em,
                                     PersistContext context)
            throws PersistenceException {
        if (isPersistent()) {
            em.remove(em.getReference(this.getClass(),this.getId()));
        }
        //Return a save result to reset the entity's state
        //to unsaved.
        return new SaveResult(UNINITIALIZED, UNINITIALIZED, null);
    }
    /********************Attributes*******************/
    /**
     * The Entity ID. This ID uniquely represents an instance
     * of this entity type in the system. The entity ID is
     * generated automatically by the ORM system.
     * 
     * @return The entity ID.
     */
    @Id
    @GeneratedValue
    public long getId() {
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
    protected void setId(long id) {
        this.id = id;
    }

    /**
     * Returns true if the entity is persisted in the
     * database.
     *
     * @return true if the entity is persisted.
     */
    @Transient
    protected boolean isPersistent() {
        return id != -1;
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
    @Version
    public int getUpdateCount() {
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
    protected void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    /**
     * The time last time this object was updated
     *
     * @return  time the object was last modified.
     */
    @Temporal(TemporalType.TIMESTAMP) //i18n_datetime when storing to / retrieving from database
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the time this object was last updated.
     * This method is invoked by {@link #setLastUpdated()}
     * whenever the entity is being saved
     * 
     * @param lastUpdated the last updated time value 
     */
    private void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Resets the timestamp to current time.
     * This method is automatically invoked by ORM
     * whenever the entity is being saved.
     */
    @PrePersist
    @PreUpdate
    private void setLastUpdated() {
        //Set lastUpdated to the current date.
        setLastUpdated(new Date()); //non-i18n
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
     * The last updated attribute name, used to refer to lastUpdated
     * attribute in various JPQL queries
     */
    protected static final String ATTRIBUTE_LAST_UPDATED = "lastUpdated"; //$NON-NLS-1$
    /**
     * The ID attribute name, used to refer to ID in various JPQL queries
     */
    public static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
    private long id = UNINITIALIZED;
    private int updateCount = UNINITIALIZED;
    private Date lastUpdated;
    protected static final int UNINITIALIZED = -1;
}
