package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Presents summary view of the entities that have
 * a name and description
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface SummaryNDEntityBase extends SummaryEntityBase {
    /**
     * The name of this entity
     * 
     * @return the name of this entity
     */
    String getName();

    /**
     * The description of this entity.
     * @return The description.
     */
    String getDescription();
}
