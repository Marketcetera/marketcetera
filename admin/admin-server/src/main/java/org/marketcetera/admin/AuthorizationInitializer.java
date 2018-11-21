package org.marketcetera.admin;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.marketcetera.admin.impl.SimpleUser;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes the authorization system.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
public class AuthorizationInitializer
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(authzService);
        Validate.notNull(userService);
        Validate.notNull(permissionFactory);
        Validate.notNull(roleFactory);
        if(users != null) {
            for(UserDescriptor userDescriptor : users) {
                if(userService.findByName(userDescriptor.getName()) == null) {
                    SLF4JLoggerProxy.info(this,
                                          "Adding user {}",
                                          userDescriptor);
                    SimpleUser user = new SimpleUser();
//                    user.setActive(userDescriptor.getIsActive());
//                    user.setDescription(userDescriptor.getDescription());
//                    user.setName(userDescriptor.getName());
//                    user.setPassword(userDescriptor.getPassword().toCharArray());
//                    user.setSuperuser(userDescriptor.getIsSuperuser());
//                    userService.save(user);
                    throw new UnsupportedOperationException(); // TODO
                } else {
                    SLF4JLoggerProxy.info(this,
                                          "Not adding user {} because a user by that name already exists",
                                          userDescriptor);
                }
            }
        }
        if(permissions != null) {
            for(PermissionDescriptor permissionDescriptor : permissions) {
                if(authzService.findPermissionByName(permissionDescriptor.getName()) == null) {
                    SLF4JLoggerProxy.info(this,
                                          "Adding permission {}",
                                          permissionDescriptor);
                    authzService.save(permissionFactory.create(permissionDescriptor.getName(),
                                                               permissionDescriptor.getDescription()));
                } else {
                    SLF4JLoggerProxy.info(this,
                                          "Not adding permission {} because a permission by that name already exists",
                                           permissionDescriptor);
                }
            }
        }
        if(roles != null) {
            for(RoleDescriptor roleDescriptor : roles) {
                if(authzService.findRoleByName(roleDescriptor.getName()) == null) {
                    Role role = roleFactory.create(roleDescriptor.getName(),
                                                   roleDescriptor.getDescription());
                    for(String permissionName : roleDescriptor.getPermissionNames()) {
                        Permission permission = authzService.findPermissionByName(permissionName);
                        if(permission != null) {
                            SLF4JLoggerProxy.info(this,
                                                  "Adding role {}",
                                                  roleDescriptor);
                            role.getPermissions().add(permission);
                        } else {
                            SLF4JLoggerProxy.warn(this,
                                                  "Not adding {} to role {} because no permission by that name exists",
                                                  permissionName,
                                                  role);
                        }
                    }
                    for(String username : roleDescriptor.getUsernames()) {
                        User user = userService.findByName(username);
                        if(user != null) {
                            role.getSubjects().add(user);
                        } else {
                            SLF4JLoggerProxy.warn(this,
                                                  "Not adding {} to role {} because no user by that name exists",
                                                  username,
                                                  role);
                        }
                    }
                    authzService.save(role);
                } else {
                    SLF4JLoggerProxy.info(this,
                                          "Not adding or modifying role {} because a role by that name already exists",
                                          roleDescriptor);
                }
            }
        }
        if(supervisorPermissions != null) {
            for(SupervisorPermissionDescriptor supervisorDescriptor: supervisorPermissions) {
                if(authzService.findSupervisorPermissionByName(supervisorDescriptor.getName()) == null) {
                    SupervisorPermission supervisorPermission = supervisorPermissionFactory.create(supervisorDescriptor.getName(),
                                                                                                   supervisorDescriptor.getDescription());
                    User supervisor = userService.findByName(supervisorDescriptor.getSupervisorUsername());
                    if(supervisor == null) {
                        SLF4JLoggerProxy.warn(this,
                                              "Not adding {} because no supervisor user by name {} exists",
                                              supervisorDescriptor,
                                              supervisorDescriptor.getSupervisorUsername());
                        continue;
                    } else {
                        supervisorPermission.setSupervisor(supervisor);
                    }
                    for(String permissionName : supervisorDescriptor.getPermissionNames()) {
                        Permission permission = authzService.findPermissionByName(permissionName);
                        if(permission != null) {
                            SLF4JLoggerProxy.info(this,
                                                  "Adding supervisor permission {}",
                                                  supervisorDescriptor);
                            supervisorPermission.getPermissions().add(permission);
                        } else {
                            SLF4JLoggerProxy.warn(this,
                                                  "Not adding {} to supervisor permission {} because no permission by that name exists",
                                                  permissionName,
                                                  supervisorPermission);
                        }
                    }
                    for(String username : supervisorDescriptor.getSubjectUsernames()) {
                        User user = userService.findByName(username);
                        if(user != null) {
                            supervisorPermission.getSubjects().add(user);
                        } else {
                            SLF4JLoggerProxy.warn(this,
                                                  "Not adding {} to supervisor permission {} because no user by that name exists",
                                                  username,
                                                  supervisorPermission);
                        }
                    }
                    authzService.save(supervisorPermission);
                } else {
                    SLF4JLoggerProxy.info(this,
                                          "Not adding or modifying supervisor permission {} because a supervisor permission by that name already exists",
                                          supervisorDescriptor);
                }
            }
        }
    }
    /**
     * Get the users value.
     *
     * @return a <code>Set&lt;UserDescriptor&gt;</code> value
     */
    public Set<UserDescriptor> getUsers()
    {
        return users;
    }
    /**
     * Sets the users value.
     *
     * @param inUsers a <code>Set&lt;UserDescriptor&gt;</code> value
     */
    public void setUsers(Set<UserDescriptor> inUsers)
    {
        users = inUsers;
    }
    /**
     * Get the permissions value.
     *
     * @return a <code>Set&lt;PermissionDescriptor&gt;</code> value
     */
    public Set<PermissionDescriptor> getPermissions()
    {
        return permissions;
    }
    /**
     * Sets the permissions value.
     *
     * @param inPermissions a <code>Set&lt;PermissionDescriptor&gt;</code> value
     */
    public void setPermissions(Set<PermissionDescriptor> inPermissions)
    {
        permissions = inPermissions;
    }
    /**
     * Get the authzService value.
     *
     * @return an <code>AuthorizationService</code> value
     */
    public AuthorizationService getAuthzService()
    {
        return authzService;
    }
    /**
     * Sets the authzService value.
     *
     * @param inAuthzService an <code>AuthorizationService</code> value
     */
    public void setAuthzService(AuthorizationService inAuthzService)
    {
        authzService = inAuthzService;
    }
    /**
     * Get the roles value.
     *
     * @return a <code>Set&lt;RoleDescriptor&gt;</code> value
     */
    public Set<RoleDescriptor> getRoles()
    {
        return roles;
    }
    /**
     * Sets the roles value.
     *
     * @param inRoles a <code>Set&lt;RoleDescriptor&gt;</code> value
     */
    public void setRoles(Set<RoleDescriptor> inRoles)
    {
        roles = inRoles;
    }
    /**
     * Get the supervisorPermissions value.
     *
     * @return a <code>Set&lt;SupervisorPermissionDescriptor&gt;</code> value
     */
    public Set<SupervisorPermissionDescriptor> getSupervisorPermissions()
    {
        return supervisorPermissions;
    }
    /**
     * Sets the supervisorPermissions value.
     *
     * @param inSupervisorPermissions a <code>Set&lt;SupervisorPermissionDescriptor&gt;</code> value
     */
    public void setSupervisorPermissions(Set<SupervisorPermissionDescriptor> inSupervisorPermissions)
    {
        supervisorPermissions = inSupervisorPermissions;
    }
    /**
     * Get the permissionFactory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    public PermissionFactory getPermissionFactory()
    {
        return permissionFactory;
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
     * Get the roleFactory value.
     *
     * @return a <code>RoleFactory</code> value
     */
    public RoleFactory getRoleFactory()
    {
        return roleFactory;
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
     * Get the userService value.
     *
     * @return a <code>UserService</code> value
     */
    public UserService getUserService()
    {
        return userService;
    }
    /**
     * Sets the userService value.
     *
     * @param inUserService a <code>UserService</code> value
     */
    public void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * creates <code>Permission</code> objects
     */
    @Autowired
    private PermissionFactory permissionFactory;
    /**
     * creates <code>Role</code> objects
     */
    @Autowired
    private RoleFactory roleFactory;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * permissions to add
     */
    private Set<PermissionDescriptor> permissions;
    /**
     * roles to add
     */
    private Set<RoleDescriptor> roles;
    /**
     * users to add
     */
    private Set<UserDescriptor> users;
    /**
     * supervisors to add
     */
    private Set<SupervisorPermissionDescriptor> supervisorPermissions;
    /**
     * creates <code>SupervisorPermission</code> objects
     */
    @Autowired
    private SupervisorPermissionFactory supervisorPermissionFactory;
}
