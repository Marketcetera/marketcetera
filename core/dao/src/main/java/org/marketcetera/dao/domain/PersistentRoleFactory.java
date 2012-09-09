package org.marketcetera.dao.domain;

import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.RoleFactory;

/**
 * @version $Id$
 * @date 9/1/12 7:33 PM
 */

public class PersistentRoleFactory implements RoleFactory {

    @Override
    public Role create(String inRolename) {
        PersistentRole role = new PersistentRole();
        role.setName(inRolename);
        return role;
    }

    @Override
    public Role create() {
        return new PersistentRole();
    }
}
