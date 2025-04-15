package org.marketcetera.admin.service.impl;

import static org.marketcetera.admin.Messages.INVALID_PASSWORD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.User;
import org.marketcetera.admin.dao.UserDao;
import org.marketcetera.admin.service.PasswordService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.user.PersistentUser;
// import org.marketcetera.admin.user.QPersistentUser; // TEMPORARILY COMMENTED OUT FOR SPRING BOOT 3 MIGRATION
// TEMPORARILY COMMENTED OUT FOR SPRING BOOT 3 MIGRATION
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQuery;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.persist.ValidationException;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
// TEMPORARILY COMMENTED OUT FOR SPRING BOOT 3 MIGRATION
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQuery;

/* $License$ */

/**
 * Provides access to user objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.2
 */
@ClassVersion("$Id$")
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
public class UserServiceImpl
        implements UserService
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#listUsers(java.lang.String, java.lang.Boolean)
     */
    @Override
    public List<? extends User> listUsers(String inNameFilter,
                                          Boolean inActiveFilter)
    {
        // TEMPORARILY ADAPTED FOR SPRING BOOT 3 MIGRATION - using Spring Data JPA instead of QueryDSL
        inNameFilter = StringUtils.trimToNull(inNameFilter);
        
        // Use userDao to fetch users
        if (inNameFilter == null && inActiveFilter == null) {
            return userDao.findAll();
        } else if (inNameFilter != null && inActiveFilter == null) {
            if (inNameFilter.contains("*") || inNameFilter.contains("?")) {
                String likePattern = inNameFilter.replaceAll("\\*", "%").replaceAll("\\?", "_");
                return userDao.findByNameLike(likePattern);
            } else {
                return userDao.findByNameAsList(inNameFilter);
            }
        } else if (inNameFilter == null && inActiveFilter != null) {
            return userDao.findByActive(inActiveFilter);
        } else {
            if (inNameFilter.contains("*") || inNameFilter.contains("?")) {
                String likePattern = inNameFilter.replaceAll("\\*", "%").replaceAll("\\?", "_");
                return userDao.findByNameLikeAndActive(likePattern, inActiveFilter);
            } else {
                return userDao.findByNameAndActive(inNameFilter, inActiveFilter);
            }
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findByName(java.lang.String)
     */
    @Override
    public PersistentUser findByName(String inUsername)
    {
        try {
            return usersByUsername.getUnchecked(inUsername);
        } catch (InvalidCacheLoadException e) {
            return null;
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findByUserId(org.marketcetera.trade.UserID)
     */
    @Override
    public PersistentUser findByUserId(UserID inUserId)
    {
        try {
            return usersByUserId.getUnchecked(inUserId);
        } catch (InvalidCacheLoadException e) {
            return null;
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#updateUserDataByName(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void updateUserDataByName(String inUsername,
                                     String inUserData)
    {
        userDao.updateUserByName(inUsername,
                                 inUserData);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#updateUserActiveStatus(java.lang.String, boolean)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void updateUserActiveStatus(String inUsername,
                                       boolean inIsActive)
    {
        userDao.updateUserActiveStatus(inUsername,
                                       inIsActive);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#save(com.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentUser save(User inUser)
    {
        PersistentUser pUser;
        if(inUser instanceof PersistentUser) {
            pUser = (PersistentUser)inUser;
        } else {
            pUser = new PersistentUser(inUser);
        }
        return userDao.save(pUser);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#delete(com.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(User inUser)
    {
        Optional<PersistentUser> pUser = userDao.findById(inUser.getId());
        if(pUser.isPresent()) {
            userDao.delete(pUser.get());
            usersByUserId.invalidate(inUser.getUserID());
            usersByUsername.invalidate(inUser.getName());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.UserService#changeUserPassword(org.marketcetera.admin.User, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentUser changeUserPassword(User inUser,
                                             String inOldRawPassword,
                                             String inNewRawPassword)
    {
        PersistentUser result = userDao.findByName(inUser.getName());
        if(result == null) {
            throw new IllegalArgumentException("Unknown user: " + inUser.getName());
        }
        if(!passwordService.matches(inOldRawPassword,result.getHashedPassword())) {
            throw new ValidationException(INVALID_PASSWORD);
        }
        result.setHashedPassword(passwordService.getHash(inNewRawPassword));
        result = userDao.save(result);
        usersByUserId.invalidate(inUser.getUserID());
        usersByUsername.invalidate(inUser.getName());
        return result;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.UserService#resetUserPassword(org.marketcetera.admin.User, java.lang.String)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public User resetUserPassword(User inUser,
                                  String inNewPassword)
    {
        PersistentUser result = userDao.findByName(inUser.getName());
        if(result == null) {
            throw new IllegalArgumentException("Unknown user: " + inUser.getName());
        }
        result.setHashedPassword(passwordService.getHash(inNewPassword));
        result = userDao.save(result);
        usersByUserId.invalidate(inUser.getUserID());
        usersByUsername.invalidate(inUser.getName());
        return result;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findOne(long)
     */
    @Override
    public PersistentUser findOne(long inValue)
    {
        return userDao.findById(inValue).orElse(null);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findAll()
     */
    @Override
    public List<PersistentUser> findAll()
    {
        return userDao.findAll();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findAll(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<User> findAll(PageRequest inPageRequest)
    {
        List<User> users = new ArrayList<>();
        Sort jpaSort = null;
        if(inPageRequest.getSortOrder() == null || inPageRequest.getSortOrder().isEmpty()) {
            // TEMPORARILY MODIFIED FOR SPRING BOOT 3 MIGRATION (removed QueryDSL reference)
            jpaSort = Sort.by(new Sort.Order(Sort.Direction.ASC, "name"));
        } else {
            for(org.marketcetera.persist.Sort sort : inPageRequest.getSortOrder()) {
                Sort.Direction jpaSortDirection = sort.getDirection()==SortDirection.ASCENDING?Sort.Direction.ASC:Sort.Direction.DESC;
                String property = sort.getProperty();
                String path = userAliases.get(property.toLowerCase());
                if(path == null) {
                    SLF4JLoggerProxy.warn(this,
                                          "No alias for user column '{}'",
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
        Page<PersistentUser> result = userDao.findAll(pageRequest);
        CollectionPageResponse<User> response = new CollectionPageResponse<>();
        response.setPageMaxSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setPageSize(result.getNumberOfElements());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        for(PersistentUser user : result.getContent()) {
            users.add(user);
        }
        response.setSortOrder(inPageRequest.getSortOrder());
        response.setElements(users);
        return response;
    }
    /**
     * Get the userDao value.
     *
     * @return a <code>SimpleUserRepository</code> value
     */
    public UserDao getUserDao()
    {
        return userDao;
    }
    /**
     * Sets the userDao value.
     *
     * @param inUserDao a <code>SimpleUserRepository</code> value
     */
    @Autowired
    public void setUserDao(UserDao inUserDao)
    {
        userDao = inUserDao;
    }
    /**
     * Get the entityManager value.
     *
     * @return an <code>EntityManager</code> value
     */
    public EntityManager getEntityManager()
    {
        return entityManager;
    }
    /**
     * Sets the entityManager value.
     *
     * @param inEntityManager an <code>EntityManager</code> value
     */
    @PersistenceContext
    public void setEntityManager(EntityManager inEntityManager)
    {
        entityManager = inEntityManager;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        if(userAliases == null) {
            userAliases = Maps.newHashMap();
            // TEMPORARILY MODIFIED FOR SPRING BOOT 3 MIGRATION (removed QueryDSL references)
            userAliases.put("name", "name");
            userAliases.put("description", "description");
            userAliases.put("active", "active");
        }
        usersByUserId = CacheBuilder.newBuilder().maximumSize(100).build(new CacheLoader<UserID,PersistentUser>() {
            @Override
            public PersistentUser load(UserID inKey)
                    throws Exception
            {
                return userDao.findById(inKey.getValue()).orElse(null);
            }}
        );
        usersByUsername = CacheBuilder.newBuilder().maximumSize(100).build(new CacheLoader<String,PersistentUser>() {
            @Override
            public PersistentUser load(String inKey)
                    throws Exception
            {
                return userDao.findByName(inKey);
            }}
        );
        SLF4JLoggerProxy.info(this,
                              "User service started");
    }
    /**
     * provides datastore access to user objects
     */
    private UserDao userDao;
    /**
     * provides direct access to the persistence context entity manager
     */
    private EntityManager entityManager;
    /**
     * stores users by user id
     */
    private LoadingCache<UserID,PersistentUser> usersByUserId;
    /**
     * stores users by username
     */
    private LoadingCache<String,PersistentUser> usersByUsername;
    /**
     * specifies column aliases to use when sorting or filtering the user table
     */
    private Map<String,String> userAliases;
    /**
     * provides access to password services
     */
    @Autowired
    private PasswordService passwordService;
}
