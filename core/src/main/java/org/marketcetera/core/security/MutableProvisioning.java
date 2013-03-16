package org.marketcetera.core.security;

import java.util.List;
import java.util.Set;

import org.marketcetera.api.systemmodel.Permission;
import org.marketcetera.api.systemmodel.Role;

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
