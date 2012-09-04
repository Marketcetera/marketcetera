package org.marketcetera.dao.impl;

import java.util.Set;

import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.User;
import org.marketcetera.core.systemmodel.*;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.core.util.misc.Initializer;
import org.marketcetera.dao.DataAccessService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;

/* $License$ */

/**
 * Initializes the users data store.
 * 
 * @version $Id$
 * @since $Release$
 */
public class UserInitializer
        implements Initializer
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.impl.Initializer#initialize()
     */
    @Override
    public void initialize()
    {
        try {
            // assign default permissions to default groups
            Role adminRole = dataService.getRoleDao().getByName(SystemRole.ADMINISTRATORS.name());
            // add admin permission to admin group
            Permission adminPermission = dataService.getPermissionDao().getByName(SystemPermission.ROLE_ADMIN.name());
            adminRole.getPermissions().add(adminPermission);
            // update admin group
            dataService.getRoleDao().save(adminRole);
            // add user permission to user group
            Role userRole = dataService.getRoleDao().getByName(SystemRole.USERS.name());
            Permission userPermission = dataService.getPermissionDao().getByName(SystemPermission.ROLE_USER.name());
            userRole.getPermissions().add(userPermission);
            dataService.getRoleDao().save(userRole);
            // add users as specified
            if(users != null) {
                for(UserSpecification spec : users) {
                    try {
                        execute(spec);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(UserInitializer.class,
                                              e,
                                              "Could not add {}",
                                              spec);
                    }
                }
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(UserInitializer.class,
                                  e,
                                  "Could not initialize permissions, quitting"); // TODO
            return;
        } finally {
            // remove temp authentication
//            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }
    /**
     * Get the users value.
     *
     * @return a <code>Set&lt;UserSpecification&gt;</code> value
     */
    public Set<UserSpecification> getUsers()
    {
        return users;
    }
    /**
     * Sets the users value.
     *
     * @param inUsers <code>Set<UserSpecification></code> value
     */
    public void setUsers(Set<UserSpecification> inUsers)
    {
        users = inUsers;
    }
    /**
     * Executes the given specification. 
     *
     * @param inUserSpecification a <code>UserSpecification</code> value
     */
    private void execute(UserSpecification inUserSpecification)
    {
//        String password = passwordEncoder.encode(inUserSpecification.getPassword());
        String password = inUserSpecification.getPassword();
        User user = userFactory.create(inUserSpecification.getUsername(), password);
        dataService.getUserDao().add(user);
        SLF4JLoggerProxy.info(UserInitializer.class,
                              "{} created",
                              user);
        for(String group : inUserSpecification.getRoles()) {
            addUserToRole(user,
                           group);
        }
    }
    /**
     * Adds the given user to the group with the given name.
     *
     * @param inUser a <code>User</code> value
     * @param inRoleName a <code>String</code> value
     */
    private void addUserToRole(User inUser,
                                String inRoleName)
    {
        Role group = dataService.getRoleDao().getByName(inRoleName);
        if(group != null) {
            group.getUsers().add(inUser);
            dataService.getRoleDao().save(group);
            SLF4JLoggerProxy.info(UserInitializer.class,
                                  "{} added to {}",
                                  inUser,
                                  group);
        } else {
            SLF4JLoggerProxy.warn(UserInitializer.class,
                                  "Cannot add {} to {} because group does not exist",
                                  inUser,
                                  inRoleName);
        }
    }
    /**
     * users to create
     */
    private Set<UserSpecification> users;
    /**
     * provides data services
     */
    private DataAccessService dataService;
    /**
     * constructs user objects
     */
    private UserFactory userFactory;
    /**
     * password encoder value
     */
//    private PasswordEncoder passwordEncoder;
}
