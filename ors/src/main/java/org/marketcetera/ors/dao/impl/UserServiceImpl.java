package org.marketcetera.ors.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.ors.dao.UserDao;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.security.QSimpleUser;
import org.marketcetera.ors.security.SimpleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@Transactional(readOnly=true)
public class UserServiceImpl
        implements UserService
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#listUsers(java.lang.String, java.lang.Boolean)
     */
    @Override
    public List<SimpleUser> listUsers(String inNameFilter,
                                      Boolean inActiveFilter)
    {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QSimpleUser simpleUser = QSimpleUser.simpleUser;
        inNameFilter = StringUtils.trimToNull(inNameFilter);
        BooleanExpression wherePredicate = null;
        if(inNameFilter != null) {
            wherePredicate = simpleUser.name.eq(inNameFilter);
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
        return jpaQuery.fetchAll().list(simpleUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#findByName(java.lang.String)
     */
    @Override
    public SimpleUser findByName(String inUsername)
    {
        return userDao.findByName(inUsername);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#updateUserDataByName(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly=false)
    public void updateUserDataByName(String inUsername,
                                     String inUserData)
    {
        userDao.updateUserByName(inUsername,
                                 inUserData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#updateUserActiveStatus(java.lang.String, boolean)
     */
    @Override
    @Transactional(readOnly=false)
    public SimpleUser updateUserActiveStatus(String inUsername,
                                             boolean inIsActive)
    {
        return userDao.updateUserActiveStatus(inUsername,
                                              inIsActive);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#updateSuperUser(java.lang.String, boolean)
     */
    @Override
    @Transactional(readOnly=false)
    public void updateSuperUser(String inUsername,
                                boolean inIsSuperuser)
    {
        userDao.updateSuperUser(inUsername,
                                inIsSuperuser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#save(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false)
    public SimpleUser save(SimpleUser inUser)
    {
        return userDao.save(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#delete(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false)
    public void delete(SimpleUser inUser)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#findAll()
     */
    @Override
    public List<SimpleUser> findAll()
    {
        return userDao.findAll();
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
    public void setEntityManager(EntityManager inEntityManager)
    {
        entityManager = inEntityManager;
    }
    /**
     * provides datastore access to user objects
     */
    @Autowired
    private UserDao userDao;
    /**
     * provides direct access to the persistence context entity manager
     */
    @PersistenceContext
    private EntityManager entityManager;
}
