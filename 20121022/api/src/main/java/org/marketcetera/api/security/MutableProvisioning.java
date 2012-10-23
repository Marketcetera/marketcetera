package org.marketcetera.api.security;

import java.util.List;
import java.util.Set;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.Role;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableProvisioning
        extends Provisioning
{
    public void setUsers(Set<User> inUsers);
    public void setRoles(Set<Role> inRoles);
    public void setPermissions(Set<Permission> inPermissions);
    public void setAssignments(List<AssignToRole> inAssignments);
}
