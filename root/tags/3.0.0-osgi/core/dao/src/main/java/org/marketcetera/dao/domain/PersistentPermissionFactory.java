package org.marketcetera.dao.domain;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionFactory;

/**
 * @version $Id$
 * @date 9/1/12 7:34 PM
 */

public class PersistentPermissionFactory implements PermissionFactory {

    @Override
    public Permission create(String inPermissionName) {
        PersistentPermission permission = new PersistentPermission();
        permission.setName(inPermissionName);
        return permission;
    }

    @Override
    public Permission create() {
        return new PersistentPermission();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.PermissionFactory#create(org.marketcetera.api.dao.Permission)
     */
    @Override
    public Permission create(Permission inPermission)
    {
        if(inPermission instanceof PersistentPermission) {
            return inPermission;
        }
        return new PersistentPermission(inPermission);
    }
}
