package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import java.io.Serializable;
import java.util.Date;
/* $License$ */
/**
 * Instances of this class are used to communicate entity
 * state changes made when persisting it back to the remote
 * instance of the entity.
 * <p>
 * Typically a new entity is assigned a new ID when its persisteed.
 * Also, the entity's updateCount and last updated timestamps is updated
 * every time its saved. The value of these attributes needs to be
 * communicated back to the client-side so that the entity instance
 * can be refreshed with the new values for attributes. Also, changes
 * to these attributes need to be undone, if the entity persistence
 * operations fails for whatever reasons.
 * <p>
 * Entity subclasses may subclass this class to include any other
 * attributes that get automatically updated during a persistence
 * operation. To be able to create and use a subclass of SaveResult
 * instead of the Entity subclass will need to over-ride the
 * {@link org.marketcetera.persist.EntityBase#createSaveResult()} to
 * create the subclass instance and over-ride the
 * {@link EntityBase#applyRemote(SaveResult)} to apply the subclass'
 * contents to the entity.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class SaveResult implements Serializable {
    private static final long serialVersionUID = 2308399196969481872L;

    /**
     * Creates an instance
     *
     * @param id The entity ID
     * @param updateCount The entity's update count
     * @param timestamp The entity's last updated time stamp value
     */
    public SaveResult(long id, int updateCount, Date timestamp) {
        this.id = id;
        this.updateCount = updateCount;
        this.timestamp = timestamp;
    }

    /**
     * Gets the entity's ID value
     *
     * @return the entity's ID
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the entity's update count value
     *
     * @return the entity's update count
     */
    public int getUpdateCount() {
        return updateCount;
    }

    /**
     * Gets the entity's last updated timestamp value
     * 
     * @return the entity's last updated timestamp value.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return "SaveResult{" +  //$NON-NLS-1$
                "id=" + id +  //$NON-NLS-1$
                ", updateCount=" + updateCount +  //$NON-NLS-1$
                ", timestamp=" + timestamp +  //$NON-NLS-1$
                '}';
    }

    private final long id;
    private final int updateCount;
    private final Date timestamp;
}
