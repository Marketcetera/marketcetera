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
public interface Provisioning
{
    public Set<User> getUsers();
    public Set<Role> getRoles();
    public Set<Permission> getPermissions();
    public List<AssignToRole> getAssignments();
    public MutableProvisioning getMutableView();
}
