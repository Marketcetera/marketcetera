package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * A query that fetches single instance of an entity given its name or ID.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class SingleNDEntityQuery
        extends SingleEntityQuery {
    private static final long serialVersionUID = -1480564878674121720L;

    /**
     * Creates an instance.
     * 
     * @param entityName The entity's JPQL name
     * @param id The entity's ID value.
     */
    protected SingleNDEntityQuery(String entityName,
                                  long id) {
        super(entityName, id);
    }

    /**
     * Creates an instance.
     *
     * @param entityName The entity's JPQL name
     * @param name The entity's ID value
     */
    protected SingleNDEntityQuery(String entityName, String name) {
        super(entityName, NDEntityBase.ATTRIBUTE_NAME,name);
    }
}
