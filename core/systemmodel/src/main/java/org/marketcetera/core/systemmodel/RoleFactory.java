package org.marketcetera.core.systemmodel;

/* $License$ */

import org.marketcetera.api.dao.Role;

/**
 * Creates <code>Role</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
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
