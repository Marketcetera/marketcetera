package org.marketcetera.api.dao;

/* $License$ */

import org.marketcetera.api.dao.Role;

/**
 * Creates <code>Role</code> objects.
 *
 * @version $Id: RoleFactory.java 82315 2012-03-17 01:58:54Z colin $
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
