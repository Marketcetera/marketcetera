package com.marketcetera.admin.impl;

import com.marketcetera.admin.PermissionFactory;

/* $License$ */

/**
 * Creates {@link PersistentPermission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentPermissionFactory.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public class PersistentPermissionFactory
        implements PermissionFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.PermissionFactory#create(java.lang.String, java.lang.String)
     */
    @Override
    public PersistentPermission create(String inName,
                                       String inDescription)
    {
        PersistentPermission permission = new PersistentPermission();
        permission.setName(inName);
        permission.setDescription(inDescription);
        return permission;
    }
}
