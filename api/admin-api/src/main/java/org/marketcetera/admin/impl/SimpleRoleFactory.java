package org.marketcetera.admin.impl;

import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;

/* $License$ */

/**
 * Creates {@link Role} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleRoleFactory
        implements RoleFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.RoleFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public Role create(String inName,
                       String inDescription)
    {
        SimpleRole role = new SimpleRole();
        role.setName(inName);
        role.setDescription(inDescription);
        return role;
    }
}
