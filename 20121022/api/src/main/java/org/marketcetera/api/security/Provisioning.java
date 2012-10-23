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
public interface Provisioning
{
    public Set<User> getUsers();
    public Set<Role> getRoles();
    public Set<Permission> getPermissions();
    public List<AssignToRole> getAssignments();
    public MutableProvisioning getMutableView();
}
