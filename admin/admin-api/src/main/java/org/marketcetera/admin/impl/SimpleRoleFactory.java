package org.marketcetera.admin.impl;

import org.marketcetera.admin.MutableRole;
import org.marketcetera.admin.MutableRoleFactory;
import org.marketcetera.admin.Role;

/* $License$ */

/**
 * Creates {@link Role} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleRoleFactory
        implements MutableRoleFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableRoleFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public MutableRole create(String inName,
                              String inDescription)
    {
        SimpleRole role = new SimpleRole();
        role.setName(inName);
        role.setDescription(inDescription);
        return role;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableRoleFactory#create()
     */
    @Override
    public MutableRole create()
    {
        return new SimpleRole();
    }
}
