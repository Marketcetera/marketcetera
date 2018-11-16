package org.marketcetera.ors.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.admin.User;
import org.marketcetera.ors.dao.UserDao;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.security.ORSLoginModule;
import org.marketcetera.ors.security.QSimpleUser;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

/* $License$ */

/**
 * Provides access to user objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserServiceImpl.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
@ClassVersion("$Id: UserServiceImpl.java 17266 2017-04-28 14:58:00Z colin $")
public class UserServiceImpl
        implements UserService, InitializingBean
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#listUsers(java.lang.String, java.lang.Boolean)
     */
    @Override
    public List<SimpleUser> listUsers(String inNameFilter,
                                      Boolean inActiveFilter)
    {
        JPAQuery<SimpleUser> jpaQuery = new JPAQuery<>(entityManager);
        QSimpleUser simpleUser = QSimpleUser.simpleUser;
        inNameFilter = StringUtils.trimToNull(inNameFilter);
        BooleanExpression wherePredicate = null;
        if(inNameFilter != null) {
            // prepare name filter, check for presence of wildcards
            if(inNameFilter.contains("*") || inNameFilter.contains("?")) {
                inNameFilter = inNameFilter.replaceAll("\\*",
                        "%").replaceAll("\\?",
                                        "_");
                wherePredicate = simpleUser.name.like(inNameFilter);
            } else {
                wherePredicate = simpleUser.name.eq(inNameFilter);
            }
        }
        if(inActiveFilter != null) {
            if(wherePredicate == null) {
                wherePredicate = simpleUser.active.eq(inActiveFilter);
            } else {
                wherePredicate = wherePredicate.and(simpleUser.active.eq(inActiveFilter));
            }
        }
        jpaQuery = jpaQuery.from(simpleUser);
        if(wherePredicate != null) {
            jpaQuery = jpaQuery.where(wherePredicate);
        }
        return jpaQuery.fetchAll().fetch();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findByName(java.lang.String)
     */
    @Override
    public SimpleUser findByName(String inUsername)
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
    public SimpleUser findByUserId(UserID inUserId)
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
    public SimpleUser save(SimpleUser inUser)
    {
        return userDao.save(inUser);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#delete(com.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(SimpleUser inUser)
    {
        inUser = userDao.findOne(inUser.getId());
        if(inUser != null) {
            userDao.delete(inUser);
            usersByUserId.invalidate(inUser.getUserID());
            usersByUsername.invalidate(inUser.getName());
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findOne(long)
     */
    @Override
    public SimpleUser findOne(long inValue)
    {
        return userDao.findOne(inValue);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.dao.UserService#findAll()
     */
    @Override
    public List<SimpleUser> findAll()
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
            jpaSort = new Sort(new Sort.Order(Sort.Direction.ASC,
                                              QSimpleUser.simpleUser.name.getMetadata().getName()));
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
                    jpaSort = new Sort(new Sort.Order(jpaSortDirection,
                                                      path));
                } else {
                    jpaSort = jpaSort.and(new Sort(new Sort.Order(jpaSortDirection,
                                                                  path)));
                }
            }
        }
        org.springframework.data.domain.PageRequest pageRequest = new org.springframework.data.domain.PageRequest(inPageRequest.getPageNumber(),
                                                                                                                  inPageRequest.getPageSize(),
                                                                                                                  jpaSort);
        Page<SimpleUser> result = userDao.findAll(pageRequest);
        CollectionPageResponse<User> response = new CollectionPageResponse<>();
        response.setPageMaxSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setPageSize(result.getNumberOfElements());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        for(SimpleUser user : result.getContent()) {
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
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        ORSLoginModule.setUserService(this);
        ORSLoginModule.setTxManager(txManager);
        if(userAliases == null) {
            userAliases = Maps.newHashMap();
            userAliases.put("name",
                            QSimpleUser.simpleUser.name.getMetadata().getName());
            userAliases.put("description",
                            QSimpleUser.simpleUser.description.getMetadata().getName());
            userAliases.put("active",
                            QSimpleUser.simpleUser.active.getMetadata().getName());
        }
        usersByUserId = CacheBuilder.newBuilder().maximumSize(100).build(new CacheLoader<UserID,SimpleUser>() {
            @Override
            public SimpleUser load(UserID inKey)
                    throws Exception
            {
                return userDao.findOne(inKey.getValue());
            }});
        usersByUsername = CacheBuilder.newBuilder().maximumSize(100).build(new CacheLoader<String,SimpleUser>() {
            @Override
            public SimpleUser load(String inKey)
                    throws Exception
            {
                return userDao.findByName(inKey);
            }});
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
     * transaction manager value
     */
    @Autowired
    private JpaTransactionManager txManager;
    /**
     * stores users by user id
     */
    private LoadingCache<UserID,SimpleUser> usersByUserId;
    /**
     * stores users by username
     */
    private LoadingCache<String,SimpleUser> usersByUsername;
    /**
     * specifies column aliases to use when sorting or filtering the user table
     */
    private Map<String,String> userAliases;
}
