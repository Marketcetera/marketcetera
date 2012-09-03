package org.marketcetera.dao.impl;

import org.marketcetera.api.dao.Role;
import org.marketcetera.core.systemmodel.RoleFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 9/1/12 7:33 PM
 */

public class PersistentRoleFactory implements RoleFactory {

    @Override
    public Role create(String inRolename) {
        return null;
    }

    @Override
    public Role create() {
        return null;
    }
}
