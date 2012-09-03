package org.marketcetera.core.systemmodel;

/* $License$ */

import org.marketcetera.api.security.GrantedPermission;

/**
 * Describes permission levels in the system.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemPermission.java 82320 2012-04-02 17:03:23Z colin $
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
            public String getPermission()
            {
                return name();
            }
            private static final long serialVersionUID = 1L;
        };
    }
}
