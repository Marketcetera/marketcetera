package org.marketcetera.admin;

import org.marketcetera.core.Factory;

/* $License$ */

/**
 * Creates {@link Role} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
public interface RoleFactory
        extends Factory<Role>
{
    /**
     * Creates a <code>Role</code> object with the given attributes.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>Role</code> value
     */
    Role create(String inName,
                String inDescription);
}
