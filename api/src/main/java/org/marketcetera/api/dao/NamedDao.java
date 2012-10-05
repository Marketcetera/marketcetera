package org.marketcetera.api.dao;

import org.marketcetera.api.systemmodel.NamedObject;

/* $License$ */

/**
 * Provides datastore access to named objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NamedDao<Clazz extends NamedObject>
        extends Dao<Clazz>
{
    /**
     * Gets the <code>Clazz</code> corresponding to the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>User</code> value
     */
    public Clazz getByName(String inName);
}
