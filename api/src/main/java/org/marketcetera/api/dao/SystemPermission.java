package org.marketcetera.api.dao;

/* $License$ */

import java.util.Set;

import org.marketcetera.api.security.GrantedPermission;

/**
 * Describes permission levels in the system.
 *
 * @version $Id$
 * @since $Release$
 */
public enum SystemPermission
{
    /**
     * full administrative rights to change system entities
     */
    ROLE_ADMIN,
    /**
     * right to use business functions
     */
    ROLE_USER;
    /**
     * Gets this value represented as a <code>GrantedPermission</code>.
     *
     * @return a <code>GrantedPermission</code> value
     */
    public GrantedPermission getAsGrantedPermission()
    {
        return new GrantedPermission() {
            @Override
            public Set<PermissionAttribute> getMethod()
            {
                throw new UnsupportedOperationException(); // TODO
            }
            private static final long serialVersionUID = 1L;
        };
    }
}
