package org.marketcetera.dao.hibernate;


import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.marketcetera.dao.UserDao;
import org.marketcetera.dao.impl.PersistentUser;
import org.marketcetera.systemmodel.User;
import org.marketcetera.core.attributes.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides Hibernate data access for {@link User} objects.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HibernateUserDao.java 82354 2012-05-11 17:46:11Z colin $
 * @since $Release$
 */
@Repository
@Transactional(readOnly=true)
@ClassVersion("$Id: HibernateUserDao.java 82354 2012-05-11 17:46:11Z colin $")
public class HibernateUserDao
        implements UserDao
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.DaoEntity#add(java.lang.Object)
     */
    @Override
    @Transactional(readOnly=false)
    public void add(User inData)
    {
        getCurrentSession().save(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.DaoEntity#getById(long)
     */
    @Override
    public User getById(long inId)
    {
        PersistentUser user = (PersistentUser)getCurrentSession().get(PersistentUser.class,
                                                                      inId);
        if(user != null) {
            setAuthorities(user);
        }
        return user;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.DaoEntity#save(java.lang.Object)
     */
    @Override
    @Transactional(readOnly=false)
    public void save(User inData)
    {
        getCurrentSession().update(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.UserDao#getByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public User getByName(String inUsername)
    {
        Query query = getCurrentSession().getNamedQuery("findUserByUsername").setString("username",
                                                                                        inUsername);
        List<PersistentUser> results = query.list();
        // we fully expect there to be 0 or 1 user for the name
        if(results.size() > 1) {
            throw new IllegalArgumentException("Too many results"); // TODO
        }
        if(results.isEmpty()) {
            return null;
        }
        PersistentUser user = results.get(0);
        setAuthorities(user);
        return user;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String inUsername)
            throws UsernameNotFoundException
    {
        User user = getByName(inUsername);
        if(user == null) {
            throw new UsernameNotFoundException(inUsername);
        }
        return user;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.UserDao#getAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAll()
    {
        Query query = getCurrentSession().getNamedQuery("findAllUsers");
        return query.list();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.UserDao#delete(org.marketcetera.systemmodel.User)
     */
    @Override
    @Transactional(readOnly=false)
    public void delete(User inData)
    {
        getCurrentSession().delete(inData);
    }
    /**
     * Retrieves the authorities assigned to the given user and populates the user with the values.
     *
     * @param inUser a <code>PersistentUser</code> value
     */
    @SuppressWarnings("unchecked")
    private void setAuthorities(PersistentUser inUser)
    {
        Query query = getCurrentSession().getNamedQuery("findAuthoritiesByUserId").setParameter(0,
                                                                                                inUser.getId());
        inUser.setAuthorities(query.list());
    }
    /**
     * Gets the current session. 
     *
     * @return a <code>Session</code> value
     */
    private Session getCurrentSession()
    {
        return sessionFactory.getCurrentSession();
    }
    /**
     * session factory value
     */
    @Autowired
    private volatile SessionFactory sessionFactory;
}
