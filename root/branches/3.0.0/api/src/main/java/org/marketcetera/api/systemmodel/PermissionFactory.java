package org.marketcetera.api.systemmodel;

/* $License$ */

import org.marketcetera.api.systemmodel.Permission;

/**
 * Constructs {@link org.marketcetera.api.systemmodel.Permission} objects.
 *
 * @version $Id$
 * @since $Release$
 */
public interface PermissionFactory
{
    /**
     * Creates a <code>Permission</code> with the given name.
     *
     * @param inPermissionName a <code>String</code> value
     * @return a <code>Permission</code> value
     */
    public Permission create(String inPermissionName);
    /**
     * Creates a <code>Permission</code> object.
     *
     * @return a <code>Permission</code> value
     */
    public Permission create();
    /**
     * Creates a <code>Perimssion</code> object from the given <code>Permission</code>.
     *
     * @param inPermission a <code>Permission</code> value
     * @return a <code>Permission</code> value
     */
    public Permission create(Permission inPermission);
}
