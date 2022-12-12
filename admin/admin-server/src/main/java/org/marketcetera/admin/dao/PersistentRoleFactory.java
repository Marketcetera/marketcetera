package org.marketcetera.admin.dao;

import org.marketcetera.admin.MutableRoleFactory;
import org.marketcetera.admin.Role;

/* $License$ */

/**
 * Creates {@link PersistentRole} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
public class PersistentRoleFactory
        implements MutableRoleFactory
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
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableRoleFactory#create()
     */
    @Override
    public PersistentRole create()
    {
        return new PersistentRole();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Factory#create(java.lang.Object)
     */
    @Override
    public Role create(Role inRole)
    {
        return create(inRole.getName(),
                      inRole.getDescription());
    }
}
