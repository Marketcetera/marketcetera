package org.marketcetera.ors.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.marketcetera.ors.dao.UserDao;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.security.ORSLoginModule;
import org.marketcetera.ors.security.QSimpleUser;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.ValidationException;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;

/* $License$ */

/**
 * Provides access to user objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service("userService")
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
@ClassVersion("$Id$")
public class UserServiceImpl
        extends AuthorizingRealm
        implements UserService, InitializingBean, Realm
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
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
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
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void updateUserActiveStatus(String inUsername,
                                       boolean inIsActive)
    {
        userDao.updateUserActiveStatus(inUsername,
                                       inIsActive);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#save(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public SimpleUser save(SimpleUser inUser)
    {
        return userDao.save(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#delete(org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void delete(SimpleUser inUser)
    {
        userDao.delete(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#findOne(long)
     */
    @Override
    public SimpleUser findOne(long inValue)
    {
        return userDao.findOne(inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.dao.UserService#findAll()
     */
    @Override
    public List<SimpleUser> findAll()
    {
        return userDao.findAll();
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        ORSLoginModule.setUserService(this);
    }
    /* (non-Javadoc)
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection inPrincipalCollection)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken inToken)
            throws AuthenticationException
    {
        System.out.println("SHIRO: Authenticating with " + inToken.getPrincipal() + " " + inToken.getCredentials());
        final SimpleUser user = userDao.findByName((String)inToken.getPrincipal());
        if(user == null) {
            throw new AuthenticationException();
        }
        try {
            user.validatePassword((char[])inToken.getCredentials());
        } catch (ValidationException e) {
            throw new AuthenticationException();
        }
        return new AuthenticationInfo() {
            @Override
            public Object getCredentials()
            {
                return user;
            }
            @Override
            public PrincipalCollection getPrincipals()
            {
                throw new UnsupportedOperationException(); // TODO
            }
            private static final long serialVersionUID = 1L;
        };
    }
    /* (non-Javadoc)
     * @see org.apache.shiro.realm.Realm#getName()
     */
    @Override
    public String getName()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.apache.shiro.realm.Realm#supports(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    public boolean supports(AuthenticationToken inToken)
    {
        return true;
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
