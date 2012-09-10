package org.marketcetera.dao.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.api.dao.*;
import org.marketcetera.api.security.User;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.dao.domain.*;


/**
 * @version $Id$
 * @date 7/14/12 3:38 AM
 */

public class StartupBean {

    // --------------------- GETTER / SETTER METHODS ---------------------
    /**
     * Sets the permissionDao value.
     *
     * @param a <code>PermissionDao</code> value
     */
    public void setPermissionDao(PermissionDao inPermissionDao)
    {
        permissionDao = inPermissionDao;
    }
    /**
     * Sets the roleDao value.
     *
     * @param inRoleDao a <code>RoleDao</code> value
     */
    public void setRoleDao(RoleDao inRoleDao)
    {
        roleDao = inRoleDao;
    }
    /**
     * Sets the userDao value.
     *
     * @param inUserDao a <code>UserDao</code> value
     */
    public void setUserDao(UserDao inUserDao)
    {
        userDao = inUserDao;
    }
    // -------------------------- OTHER METHODS --------------------------
    public void activate() {
        SystemObjectList initialObjectList;
        try {
            InputStream inputStream = getClass().getResourceAsStream("/initialdata.xml");
            JAXBContext context = JAXBContext.newInstance(PersistentPermission.class,PersistentRole.class,PersistentUser.class,SystemObjectList.class,NameReference.class);
            Unmarshaller m = context.createUnmarshaller();
            Object rawDeserialized = m.unmarshal(new InputStreamReader(inputStream));
            if(rawDeserialized instanceof SystemObjectList) {
                initialObjectList = (SystemObjectList)rawDeserialized;
            } else {
                throw new IllegalArgumentException("Expected an element of type objectList instead of " + rawDeserialized.getClass().getCanonicalName());
            }
        } catch (JAXBException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException("Could not initialize data from initialdata.xml");
        }
        for(Permission permission : initialObjectList.getPermissions()) {
            try {
                permissionDao.add(permission);
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Error writing {}, skipping",
                                      permission);
            }
        }
        for(User user : initialObjectList.getUsers()) {
            try {
                userDao.add(user);
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Error writing {}, skipping",
                                      user);
            }
        }
        for(Role role : initialObjectList.getRoles()) {
            try {
                roleDao.add(role);
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Error writing {}, skipping",
                                      role);
            }
        }
        for(AssignToRole assignToRole : initialObjectList.getAssignToRole()) {
            SLF4JLoggerProxy.info(this,
                                  "Performing assignation {}",
                                  assignToRole);
            // the elements to assign to the role are supposed to already exist, so find them
            String roleName = StringUtils.trimToNull(assignToRole.getRoleName());
            if(roleName == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Cannot perform assignation {} because no role name was provided, skipping",
                                      assignToRole);
                continue;
            }
            MutableRole roleToModify;
            try {
                roleToModify = roleDao.getByName(roleName);
            } catch (NoResultException e) {
                SLF4JLoggerProxy.warn(this,
                                      "Cannot perform assignation {} because no role by that name exists, skipping",
                                      assignToRole);
                continue;
            }
            // retrieve the permissions to add (note that this implementation consciously chooses to remove existing permissions/users in favor of the new list)
            Set<Permission> permissionsToAdd = new HashSet<Permission>();
            for(NameReference reference : assignToRole.getPermissionReferences()) {
                String permissionReference = StringUtils.trimToNull(reference.getName());
                if(permissionReference != null) {
                    try {
                        Permission permission = permissionDao.getByName(permissionReference);
                        permissionsToAdd.add(permission);
                    } catch (NoResultException e) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot assign permission {} to role {} because no permission by that name exists",
                                              permissionReference,
                                              assignToRole);
                    }
                }
            }
            // retrieve the users to add (note that this implementation consciously chooses to remove existing permissions/users in favor of the new list)
            Set<User> usersToAdd = new HashSet<User>();
            for(NameReference reference : assignToRole.getUserReferences()) {
                String userReference = StringUtils.trimToNull(reference.getName());
                if(userReference != null) {
                    try {
                        User user = userDao.getByName(userReference);
                        usersToAdd.add(user);
                    } catch (NoResultException e) {
                        SLF4JLoggerProxy.warn(this,
                                              "Cannot assign user {} to role {} because no user by that name exists",
                                              userReference,
                                              assignToRole);
                    }
                }
            }
            // tie it all together
            roleToModify.setPermissions(permissionsToAdd);
            roleToModify.setUsers(usersToAdd);
            roleDao.save(roleToModify);
        }
        SLF4JLoggerProxy.info(this,
                              "Roles are now: {}",
                              roleDao.getAll());
    }
    // ------------------------------ FIELDS ------------------------------
    private PermissionDao permissionDao;
    private RoleDao roleDao;
    private UserDao userDao;
}
