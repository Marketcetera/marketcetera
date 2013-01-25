package org.marketcetera.newpersist;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Presents summary view of the entities that have
 * a name and description
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: SummaryNDEntityBase.java 16461 2013-01-21 22:58:07Z colin $")
public interface SummaryNDEntityBase
        extends SummaryEntityBase
{
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
