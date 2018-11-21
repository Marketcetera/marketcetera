package org.marketcetera.admin.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.marketcetera.admin.NotAuthorizedException;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.SupervisorPermission;
import org.marketcetera.admin.SupervisorPermissionFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.dao.PersistentPermission;
import org.marketcetera.admin.dao.PersistentPermissionDao;
import org.marketcetera.admin.dao.PersistentRole;
import org.marketcetera.admin.dao.PersistentRoleDao;
import org.marketcetera.admin.dao.PersistentSupervisorPermission;
import org.marketcetera.admin.dao.PersistentSupervisorPermissionDao;
import org.marketcetera.admin.dao.QPersistentPermission;
import org.marketcetera.admin.dao.QPersistentRole;
import org.marketcetera.admin.dao.UserDao;
import org.marketcetera.admin.provisioning.AdminConfiguration;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.core.Pair;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides access to authorization services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AuthorizationServiceImpl.java 84563 2015-03-31 18:39:06Z colin $
 * @since 1.0.1
 */
@Service
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
public class AuthorizationServiceImpl
        implements AuthorizationService
{
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.AuthorizationService#addPermission(com.marketcetera.tiaacref.systemmodel.Permission)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentPermission save(Permission inPermission)
    {
        PersistentPermission persistentPermission;
        if(inPermission instanceof PersistentPermission) {
            persistentPermission = (PersistentPermission)inPermission;
        } else {
            persistentPermission = new PersistentPermission(inPermission);
        }
        return permissionDao.save(persistentPermission);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#deletePermission(java.lang.String)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void deletePermission(String inPermissionName)
    {
        PersistentPermission permission = permissionDao.findByName(inPermissionName);
        if(permission == null) {
            return;
        }
        permissionDao.delete(permission);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.AuthorizationService#save(com.marketcetera.tiaacref.systemmodel.Role)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public Role save(Role inRole)
    {
        PersistentRole persistentRole;
        if(inRole instanceof PersistentRole) {
            persistentRole = (PersistentRole)inRole;
        } else {
            persistentRole = new PersistentRole(inRole);
        }
        return roleDao.save(persistentRole);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#deleteRole(java.lang.String)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void deleteRole(String inRoleName)
    {
        PersistentRole role = roleDao.findByName(inRoleName);
        if(role == null) {
            return;
        }
        roleDao.delete(role);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#authorizeNoException(java.lang.String, java.lang.String)
     */
    @Override
    public boolean authorizeNoException(String inUsername,
                                        String inPermissionName)
    {
        return permissionMapsByUsername.getUnchecked(inUsername).getUnchecked(inPermissionName);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.AuthorizationService#authorize(java.lang.String, java.lang.String)
     */
    @Override
    public void authorize(String inUsername,
                          String inPermissionName)
    {
        if(authorizeNoException(inUsername,
                                inPermissionName)) {
            SLF4JLoggerProxy.trace(this,
                                   "{} has {} permission",
                                   inUsername,
                                   inPermissionName);
        } else {
            throw new NotAuthorizedException(inUsername + " is not authorized for " + inPermissionName);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#getSupervisorsFor(java.lang.String, java.lang.String)
     */
    @Override
    public Set<User> getSupervisorsFor(String inUsername,
                                       String inPermissionName)
    {
        return supervisorsBySupervisorKey.getUnchecked(new GetSupervisorKey(inUsername,inPermissionName));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#getSubjectUsersFor(com.marketcetera.ors.security.SimpleUser, java.lang.String)
     */
    @Override
    public Set<User> getSubjectUsersFor(User inSupervisorUser,
                                        String inPermissionName)
    {
        return subjectUsersByKey.getUnchecked(new GetSubjectUsersKey(inSupervisorUser,inPermissionName));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#findSupervisorPermissionByName(java.lang.String)
     */
    @Override
    public SupervisorPermission findSupervisorPermissionByName(String inName)
    {
        return supervisorPermissionDao.findByName(inName);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#save(com.marketcetera.admin.SupervisorPermission)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public SupervisorPermission save(SupervisorPermission inSupervisorPermission)
    {
        PersistentSupervisorPermission persistentSupervisorPermission;
        if(inSupervisorPermission instanceof PersistentSupervisorPermission) {
            persistentSupervisorPermission = (PersistentSupervisorPermission)inSupervisorPermission;
        } else {
            persistentSupervisorPermission = new PersistentSupervisorPermission(inSupervisorPermission);
        }
        return supervisorPermissionDao.save(persistentSupervisorPermission);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.AuthorizationService#findAllPermissionsByUsername(java.lang.String)
     */
    @Override
    public Set<Permission> findAllPermissionsByUsername(String inUsername)
    {
        Set<Permission> permissions = new HashSet<>();
        Set<PersistentPermission> internalPermissions = permissionDao.findAllByUsername(inUsername);
        if(internalPermissions != null) {
            for(PersistentPermission internalPermission : internalPermissions) {
                permissions.add(internalPermission);
            }
        }
        return permissions;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#findAllPermissions()
     */
    @Override
    public List<Permission> findAllPermissions()
    {
        List<Permission> permissions = new ArrayList<>();
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC,
                                           QPersistentPermission.persistentPermission.name.getMetadata().getName()));
        permissions.addAll(permissionDao.findAll(sort));
        return permissions;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#findAllPermissions(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<Permission> findAllPermissions(PageRequest inPageRequest)
    {
        List<Permission> permissions = new ArrayList<>();
        Sort jpaSort = null;
        if(inPageRequest.getSortOrder() == null || inPageRequest.getSortOrder().isEmpty()) {
            jpaSort = Sort.by(new Sort.Order(Sort.Direction.ASC,
                                             QPersistentPermission.persistentPermission.name.getMetadata().getName()));
        } else {
            for(org.marketcetera.persist.Sort sort : inPageRequest.getSortOrder()) {
                Sort.Direction jpaSortDirection = sort.getDirection()==SortDirection.ASCENDING?Sort.Direction.ASC:Sort.Direction.DESC;
                String property = sort.getProperty();
                String path = permissionAliases.get(property.toLowerCase());
                if(path == null) {
                    SLF4JLoggerProxy.warn(this,
                                          "No alias for permission column '{}'",
                                          property);
                    path = property;
                }
                if(jpaSort == null) {
                    jpaSort = Sort.by(new Sort.Order(jpaSortDirection,
                                                     path));
                } else {
                    jpaSort = jpaSort.and(Sort.by(new Sort.Order(jpaSortDirection,
                                                                 path)));
                }
            }
        }
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(inPageRequest.getPageNumber(),
                                                                                                                 inPageRequest.getPageSize(),
                                                                                                                 jpaSort);
        Page<PersistentPermission> result = permissionDao.findAll(pageRequest);
        CollectionPageResponse<Permission> response = new CollectionPageResponse<>();
        response.setPageMaxSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setPageSize(result.getNumberOfElements());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        for(PersistentPermission permission : result.getContent()) {
            permissions.add(permission);
        }
        response.setSortOrder(inPageRequest.getSortOrder());
        response.setElements(permissions);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#findAllRoles()
     */
    @Override
    public List<Role> findAllRoles()
    {
        List<Role> roles = new ArrayList<>();
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC,
                                           QPersistentRole.persistentRole.name.getMetadata().getName()));
        roles.addAll(roleDao.findAll(sort));
        return roles;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.service.AuthorizationService#findAllRoles(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<Role> findAllRoles(PageRequest inPageRequest)
    {
        List<Role> roles = new ArrayList<>();
        Sort jpaSort = null;
        if(inPageRequest.getSortOrder() == null || inPageRequest.getSortOrder().isEmpty()) {
            jpaSort = Sort.by(new Sort.Order(Sort.Direction.ASC,
                                             QPersistentRole.persistentRole.name.getMetadata().getName()));
        } else {
            for(org.marketcetera.persist.Sort sort : inPageRequest.getSortOrder()) {
                Sort.Direction jpaSortDirection = sort.getDirection()==SortDirection.ASCENDING?Sort.Direction.ASC:Sort.Direction.DESC;
                String property = sort.getProperty();
                String path = roleAliases.get(property.toLowerCase());
                if(path == null) {
                    SLF4JLoggerProxy.warn(this,
                                          "No alias for role column '{}'",
                                          property);
                    path = property;
                }
                if(jpaSort == null) {
                    jpaSort = Sort.by(new Sort.Order(jpaSortDirection,
                                                     path));
                } else {
                    jpaSort = jpaSort.and(Sort.by(new Sort.Order(jpaSortDirection,
                                                                 path)));
                }
            }
        }
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(inPageRequest.getPageNumber(),
                                                                                                                 inPageRequest.getPageSize(),
                                                                                                                 jpaSort);
        Page<PersistentRole> result = roleDao.findAll(pageRequest);
        CollectionPageResponse<Role> response = new CollectionPageResponse<>();
        response.setPageMaxSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setPageSize(result.getNumberOfElements());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        for(PersistentRole permission : result.getContent()) {
            roles.add(permission);
        }
        response.setElements(roles);
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.AuthorizationService#findRoleByName(java.lang.String)
     */
    @Override
    public PersistentRole findRoleByName(String inName)
    {
        return roleDao.findByName(inName);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.irouter.service.AuthorizationService#findByName(java.lang.String)
     */
    @Override
    public PersistentPermission findPermissionByName(String inName)
    {
        return permissionDao.findByName(inName);
    }
    private void provision()
    {
        if(adminConfiguration == null) {
            SLF4JLoggerProxy.debug(this,
                                   "No provisioning to be done");
            return;
        }
        SLF4JLoggerProxy.info(this,
                              "Beginning provisioning");
        for(AdminConfiguration.User userDescriptor : adminConfiguration.getUsers()) {
            if(userService.findByName(userDescriptor.getName()) == null) {
                SLF4JLoggerProxy.info(this,
                                      "Adding user {}",
                                      userDescriptor);
                PersistentUser user = new PersistentUser();
                user.setActive(userDescriptor.getIsActive());
                user.setDescription(userDescriptor.getDescription());
                user.setName(userDescriptor.getName());
                user.setPassword(userDescriptor.getPassword().toCharArray());
                user.setSuperuser(false);
                userService.save(user);
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Not adding user {} because a user by that name already exists",
                                       userDescriptor);
            }
        }
        for(AdminConfiguration.Permission permissionDescriptor : adminConfiguration.getPermissions()) {
            if(authzService.findPermissionByName(permissionDescriptor.getName()) == null) {
                SLF4JLoggerProxy.info(this,
                                      "Adding permission {}",
                                      permissionDescriptor);
                authzService.save(permissionFactory.create(permissionDescriptor.getName(),
                                                           permissionDescriptor.getDescription()));
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "Not adding permission {} because a permission by that name already exists",
                                       permissionDescriptor);
            }
        }
        for(AdminConfiguration.Role roleDescriptor : adminConfiguration.getRoles()) {
            if(authzService.findRoleByName(roleDescriptor.getName()) == null) {
                Role role = roleFactory.create(roleDescriptor.getName(),
                                               roleDescriptor.getDescription());
                for(String permissionName : roleDescriptor.getPermissions()) {
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
                for(String username : roleDescriptor.getUsers()) {
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
                SLF4JLoggerProxy.debug(this,
                                       "Not adding or modifying role {} because a role by that name already exists",
                                       roleDescriptor);
            }
        }
        for(AdminConfiguration.SupervisorPermission supervisorDescriptor: adminConfiguration.getSupervisorPermissions()) {
            if(authzService.findSupervisorPermissionByName(supervisorDescriptor.getName()) == null) {
                SupervisorPermission supervisorPermission = supervisorPermissionFactory.create(supervisorDescriptor.getName(),
                                                                                               supervisorDescriptor.getDescription());
                User supervisor = userService.findByName(supervisorDescriptor.getSupervisorName());
                if(supervisor == null) {
                    SLF4JLoggerProxy.warn(this,
                                          "Not adding {} because no supervisor user by name {} exists",
                                          supervisorDescriptor,
                                          supervisorDescriptor.getSupervisorName());
                    continue;
                } else {
                    supervisorPermission.setSupervisor(supervisor);
                }
                for(String permissionName : supervisorDescriptor.getPermissions()) {
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
                for(String username : supervisorDescriptor.getSubjectNames()) {
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
    /**
     * Starts and validates object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(permissionDao);
        Validate.notNull(roleDao);
        provision();
        if(roleAliases == null) {
            roleAliases = Maps.newHashMap();
            roleAliases.put("name",
                            QPersistentRole.persistentRole.name.getMetadata().getName());
            roleAliases.put("description",
                            QPersistentRole.persistentRole.description.getMetadata().getName());
        }
        if(permissionAliases == null) {
            permissionAliases = Maps.newHashMap();
            permissionAliases.put("name",
                                  QPersistentPermission.persistentPermission.name.getMetadata().getName());
            permissionAliases.put("description",
                                  QPersistentPermission.persistentPermission.description.getMetadata().getName());
        }
        permissionMapsByUsername = CacheBuilder.newBuilder().expireAfterAccess(userPermissionCacheTtl,TimeUnit.MILLISECONDS).build(new CacheLoader<String,LoadingCache<String,Boolean>>() {
            @Override
            public LoadingCache<String,Boolean> load(String inUsername)
                    throws Exception
            {
                final PersistentUser user = userDao.findByName(inUsername);
                if(user == null) {
                    throw new IllegalArgumentException("Unknown user: " + inUsername);
                }
                LoadingCache<String,Boolean> permissionsByPermissionName = CacheBuilder.newBuilder().build(new CacheLoader<String,Boolean>() {
                    @Override
                    public Boolean load(String inPermissionName)
                            throws Exception
                    {
                        Permission permission = permissionDao.findByName(inPermissionName);
                        if(permission == null) {
                            throw new IllegalArgumentException("Unknown permission: " + inPermissionName);
                        }
                        Set<PersistentPermission> permissions = permissionDao.findAllByUsername(user.getName());
                        SLF4JLoggerProxy.trace(this,
                                               "Checking to see if {} has permission {} in {}",
                                               user,
                                               permission,
                                               permissions);
                        return permissions != null && permissions.contains(permission);
                    }});
                return permissionsByPermissionName;
            }});
        supervisorsBySupervisorKey = CacheBuilder.newBuilder().expireAfterAccess(userPermissionCacheTtl, TimeUnit.MILLISECONDS).build(new CacheLoader<GetSupervisorKey,Set<User>>() {
            @Override
            public Set<User> load(GetSupervisorKey inKey)
                    throws Exception
            {
                Set<User> supervisors = Sets.newHashSet();
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("findSupervisorTransaction");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                def.setReadOnly(true);
                TransactionStatus status = txManager.getTransaction(def);
                try {
                    PersistentUser subject = userDao.findByName(inKey.getUsername());
                    if(subject == null) {
                        return supervisors;
                    }
                    PersistentPermission permission = permissionDao.findByName(inKey.getPermissionName());
                    if(permission == null) {
                        return supervisors;
                    }
                    List<PersistentSupervisorPermission> allSupervisorPermissions = supervisorPermissionDao.findAll();
                    for(PersistentSupervisorPermission supervisorPermission : allSupervisorPermissions) {
                        if(supervisorPermission.getSubjects().contains(subject) && supervisorPermission.getPermissions().contains(permission)) {
                            supervisors.add(supervisorPermission.getSupervisor());
                        }
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                    try {
                        txManager.rollback(status);
                    } catch (Exception e1) {
                        SLF4JLoggerProxy.warn(this,
                                              e1);
                    } finally {
                        status = null;
                    }
                } finally {
                    if(status != null) {
                        txManager.commit(status);
                    }
                }
                return supervisors;
            }}
        );
        subjectUsersByKey = CacheBuilder.newBuilder().expireAfterAccess(userPermissionCacheTtl,TimeUnit.MILLISECONDS).build(new CacheLoader<GetSubjectUsersKey,Set<User>>() {
            @Override
            public Set<User> load(GetSubjectUsersKey inKey)
                    throws Exception
            {
                Set<User> subjects = Sets.newHashSet();
                PersistentPermission permission = permissionDao.findByName(inKey.getPermissionName());
                if(permission == null) {
                    return subjects;
                }
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("findSupervisorTransaction");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                def.setReadOnly(true);
                TransactionStatus status = txManager.getTransaction(def);
                try {
                    PersistentUser supervisor = userDao.findByName(inKey.getUser().getName());
                    if(supervisor != null) {
                        Set<PersistentSupervisorPermission> allSupervisorPermissions = supervisorPermissionDao.findBySupervisor(supervisor);
                        for(PersistentSupervisorPermission supervisorPermission : allSupervisorPermissions) {
                            if(supervisorPermission.getPermissions().contains(permission)) {
                                for(User user : supervisorPermission.getSubjects()) {
                                    subjects.add((PersistentUser)user);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                    try {
                        txManager.rollback(status);
                    } catch (Exception e1) {
                        SLF4JLoggerProxy.warn(this,
                                              e1);
                    } finally {
                        status = null;
                    }
                } finally {
                    if(status != null) {
                        txManager.commit(status);
                    }
                }
                return subjects;
            }}
        );
    }
    /**
     * Get the userPermissionCacheTtl value.
     *
     * @return a <code>long</code> value
     */
    public long getUserPermissionCacheTtl()
    {
        return userPermissionCacheTtl;
    }
    /**
     * Sets the userPermissionCacheTtl value.
     *
     * @param inUserPermissionCacheTtl a <code>long</code> value
     */
    public void setUserPermissionCacheTtl(long inUserPermissionCacheTtl)
    {
        userPermissionCacheTtl = inUserPermissionCacheTtl;
    }
    /**
     * Get the permissionDao value.
     *
     * @return a <code>PersistentPermissionDao</code> value
     */
    public PersistentPermissionDao getPermissionDao()
    {
        return permissionDao;
    }
    /**
     * Sets the permissionDao value.
     *
     * @param inPermissionDao a <code>PersistentPermissionDao</code> value
     */
    public void setPermissionDao(PersistentPermissionDao inPermissionDao)
    {
        permissionDao = inPermissionDao;
    }
    /**
     * Get the roleDao value.
     *
     * @return a <code>PersistentRoleDao</code> value
     */
    public PersistentRoleDao getRoleDao()
    {
        return roleDao;
    }
    /**
     * Sets the roleDao value.
     *
     * @param inRoleDao a <code>PersistentRoleDao</code> value
     */
    public void setRoleDao(PersistentRoleDao inRoleDao)
    {
        roleDao = inRoleDao;
    }
    /**
     * Get the supervisorPermissionDao value.
     *
     * @return a <code>PersistentSupervisorPermissionDao</code> value
     */
    public PersistentSupervisorPermissionDao getSupervisorPermissionDao()
    {
        return supervisorPermissionDao;
    }
    /**
     * Sets the supervisorPermissionDao value.
     *
     * @param inSupervisorPermissionDao a <code>PersistentSupervisorPermissionDao</code> value
     */
    public void setSupervisorPermissionDao(PersistentSupervisorPermissionDao inSupervisorPermissionDao)
    {
        supervisorPermissionDao = inSupervisorPermissionDao;
    }
    /**
     * Get the userDao value.
     *
     * @return a <code>UserDao</code> value
     */
    public UserDao getUserDao()
    {
        return userDao;
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
    /**
     * Get the roleAliases value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getRoleAliases()
    {
        return roleAliases;
    }
    /**
     * Sets the roleAliases value.
     *
     * @param inRoleAliases a <code>Map&lt;String,String&gt;</code> value
     */
    public void setRoleAliases(Map<String,String> inRoleAliases)
    {
        roleAliases = inRoleAliases;
    }
    /**
     * Get the permissionAliases value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getPermissionAliases()
    {
        return permissionAliases;
    }
    /**
     * Sets the permissionAliases value.
     *
     * @param inPermissionAliases a <code>Map&lt;String,String&gt;</code> value
     */
    public void setPermissionAliases(Map<String,String> inPermissionAliases)
    {
        permissionAliases = inPermissionAliases;
    }
    /**
     * Uniquely identifies a username/permission name tuple.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class GetSupervisorKey
            extends Pair<String,String>
    {
        /**
         * Get the username value.
         *
         * @return a <code>String</code> value
         */
        private String getUsername()
        {
            return getFirstMember();
        }
        /**
         * Get the permission value.
         *
         * @return a <code>String</code> value
         */
        private String getPermissionName()
        {
            return getSecondMember();
        }
        /**
         * Create a new GetSupervisorKey instance.
         *
         * @param inUsername a <code>String</code> value
         * @param inPermissionName a <code>String</code> value
         */
        private GetSupervisorKey(String inUsername,
                                 String inPermissionName)
        {
            super(inUsername,
                  inPermissionName);
        }
    }
    /**
     * Uniquely identifies a supervisor/user tuple.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class GetSubjectUsersKey
            extends Pair<User,String>
    {
        /**
         * Get the user value.
         *
         * @return a <code>User</code> value
         */
        private User getUser()
        {
            return getFirstMember();
        }
        /**
         * Get the permission name value.
         *
         * @return a <code>String</code> value
         */
        private String getPermissionName()
        {
            return getSecondMember();
        }
        /**
         * Create a new GetSubjectUsersKey instance.
         *
         * @param inUser a <code>User</code> value
         * @param inPermissionName a <code>String</code> value
         */
        private GetSubjectUsersKey(User inUser,
                                   String inPermissionName)
        {
            super(inUser,
                  inPermissionName);
        }
    }
    /**
     * provides datastore access to supervisor permission objects
     */
    @Autowired
    private PersistentSupervisorPermissionDao supervisorPermissionDao;
    /**
     * provides datastore access to permission objects
     */
    @Autowired
    private PersistentPermissionDao permissionDao;
    /**
     * provides datastore access to role objects
     */
    @Autowired
    private PersistentRoleDao roleDao;
    /**
     * provides datastore access to user objects
     */
    @Autowired
    private UserDao userDao;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
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
     * allows access to transactions
     */
    @Autowired
    private PlatformTransactionManager txManager;
    /**
     * creates <code>SupervisorPermission</code> objects
     */
    @Autowired
    private SupervisorPermissionFactory supervisorPermissionFactory;
    /**
     * optionally provides bootstrap provisioning
     */
    @Autowired(required=false)
    private AdminConfiguration adminConfiguration;
    /**
     * length of time to cache user permissions
     */
    private long userPermissionCacheTtl = 1000 * 60 * 5;
    /**
     * caches permissions by username and permission name
     */
    private LoadingCache<String,LoadingCache<String,Boolean>> permissionMapsByUsername;
    /**
     * caches supervisors for a user/permission tuple
     */
    private LoadingCache<GetSupervisorKey,Set<User>> supervisorsBySupervisorKey;
    /**
     * caches subjects for a supervisor/permission tuple
     */
    private LoadingCache<GetSubjectUsersKey,Set<User>> subjectUsersByKey;
    /**
     * specifies column aliases to use when sorting or filtering the role table
     */
    private Map<String,String> roleAliases;
    /**
     * specifies column aliases to use when sorting or filtering the permission table
     */
    private Map<String,String> permissionAliases;
}
