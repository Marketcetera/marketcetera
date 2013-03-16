package org.marketcetera.api.systemmodel;

/* $License$ */

import org.marketcetera.api.systemmodel.Role;

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
    /**
     * Creates a <code>Role</code> object with the same attributes as the given <code>Role</code>.
     *
     * @param inRole a <code>Role</code> value
     * @return a <code>Role</code> value
     */
    public Role create(Role inRole);
}
