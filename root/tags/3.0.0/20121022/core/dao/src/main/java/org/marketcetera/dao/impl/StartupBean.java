package org.marketcetera.dao.impl;

import java.util.EnumSet;

import org.marketcetera.api.dao.*;
import org.marketcetera.api.security.MutableAssignToRole;
import org.marketcetera.api.security.MutableProvisioning;
import org.marketcetera.api.security.ProvisioningManager;
import org.marketcetera.dao.domain.SimpleAssignToRole;
import org.marketcetera.dao.domain.SimpleProvisioning;

/* $License$ */

/**
 * Provides necesary startup behaviors
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StartupBean
{
    /**
     * Sets the userFactory value.
     *
     * @param inUserFactory a <code>UserFactory</code> value
     */
    public void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * Sets the permissionFactory value.
     *
     * @param inPermissionFactory a <code>PermissionFactory</code> value
     */
    public void setPermissionFactory(PermissionFactory inPermissionFactory)
    {
        permissionFactory = inPermissionFactory;
    }
    /**
     * Sets the roleFactory value.
     *
     * @param inRoleFactory a <code>RoleFactory</code> value
     */
    public void setRoleFactory(RoleFactory inRoleFactory)
    {
        roleFactory = inRoleFactory;
    }
    /**
     * Sets the provisioningManager value.
     *
     * @param inProvisioningManager a <code>ProvisioningManager</code> value
     */
    public void setProvisioningManager(ProvisioningManager inProvisioningManager)
    {
        provisioningManager = inProvisioningManager;
    }
    /**
     * Performs operations to initialize the system.
     */
    public void activate()
    {
        MutableProvisioning provisioning = new SimpleProvisioning();
        MutableUser adminUser = (MutableUser)userFactory.create();
        adminUser.setUsername("admin");
        adminUser.setDescription("Administrative user");
        adminUser.setIsAccountNonExpired(true);
        adminUser.setIsAccountNonLocked(true);
        adminUser.setIsCredentialsNonExpired(true);
        adminUser.setIsEnabled(true);
        adminUser.setPassword("admin");
        provisioning.getUsers().add(adminUser);
        MutablePermission userUiPermission = (MutablePermission)permissionFactory.create();
        userUiPermission.setName("user ui admin");
        userUiPermission.setDescription("Permission to perform UI administration tasks on users");
        userUiPermission.setPermission("ui:user");
        userUiPermission.setMethod(EnumSet.allOf(PermissionAttribute.class));
        provisioning.getPermissions().add(userUiPermission);
        MutablePermission permissionUiPermission = (MutablePermission)permissionFactory.create();
        permissionUiPermission.setName("permission ui admin");
        permissionUiPermission.setDescription("Permission to perform UI administration tasks on permissions");
        permissionUiPermission.setPermission("ui:permission");
        permissionUiPermission.setMethod(EnumSet.allOf(PermissionAttribute.class));
        provisioning.getPermissions().add(permissionUiPermission);
        MutablePermission roleUiPermission = (MutablePermission)permissionFactory.create();
        roleUiPermission.setName("role ui admin");
        roleUiPermission.setDescription("Permission to perform UI administration tasks on roles");
        roleUiPermission.setPermission("ui:role");
        roleUiPermission.setMethod(EnumSet.allOf(PermissionAttribute.class));
        provisioning.getPermissions().add(roleUiPermission);
        MutableRole adminRole = (MutableRole)roleFactory.create();
        adminRole.setDescription("administration role");
        adminRole.setName("admin");
        provisioning.getRoles().add(adminRole);
        MutableAssignToRole assignment = new SimpleAssignToRole();
        assignment.setRole(adminRole.getName());
        assignment.getPermissions().add(userUiPermission.getName());
        assignment.getPermissions().add(permissionUiPermission.getName());
        assignment.getPermissions().add(roleUiPermission.getName());
        assignment.getUsers().add(adminUser.getName());
        provisioning.getAssignments().add(assignment);
        provisioningManager.provision(provisioning);
    }
    /**
     * creates user objects
     */
    private UserFactory userFactory;
    /**
     * creates permission objects
     */
    private PermissionFactory permissionFactory;
    /**
     * creates role objects
     */
    private RoleFactory roleFactory;
    /**
     * provides provisioning services
     */
    private ProvisioningManager provisioningManager;
}
