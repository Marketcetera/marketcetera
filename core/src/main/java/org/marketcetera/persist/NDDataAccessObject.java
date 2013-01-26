package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface NDDataAccessObject<Clazz extends NDEntityBase>
        extends DataAccessObject<Clazz>
{
    /**
     * Gets the <code>Clazz</code> corresponding to the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>User</code> value
     * @throws NoResultException if no object matches the given name
     */
    public Clazz getByName(String inName);
}
