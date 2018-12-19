package org.marketcetera.admin.impl;

import org.marketcetera.admin.RoleFactory;

/* $License$ */

/**
 * Creates {@link PersistentRole} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
public class PersistentRoleFactory
        implements RoleFactory
{

    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.RoleFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public PersistentRole create(String inName,
                                 String inDescription)
    {
        PersistentRole role = new PersistentRole();
        role.setName(inName);
        role.setDescription(inDescription);
        return role;
    }
}
