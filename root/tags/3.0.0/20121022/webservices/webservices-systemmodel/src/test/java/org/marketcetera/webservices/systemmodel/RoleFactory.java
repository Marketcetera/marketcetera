package org.marketcetera.webservices.systemmodel;

/* $License$ */

import org.marketcetera.api.dao.Role;

/**
 * Creates <code>Role</code> objects.
 *
 * @version $Id$
 * @since $Release$
 */
public interface RoleFactory
{
    /**
     * Creates a <code>Role</code> object with the given attributes.
     *
     * @param inRolename a <code>String</code> value
     * @return a <code>Role</code> value
     */
    public Role create(String inRolename);
    /**
     * Creates a <code>Role</code> object.
     *
     * @return a <code>Role</code> value
     */
    public Role create();
}
