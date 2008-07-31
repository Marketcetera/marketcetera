package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/* $License$ */
/**
 * Presents a summary view of the entities.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface SummaryEntityBase extends Serializable {
    /**
     * The Entity ID. This ID uniquely represents an instance
     * of this entity type in the system.
     *
     * @return The entity ID.
     */
    long getId();

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
    int getUpdateCount();

    /**
     * The time last time this object was updated
     * @return  time the object was last modified.
     */
    Date getLastUpdated();
}
