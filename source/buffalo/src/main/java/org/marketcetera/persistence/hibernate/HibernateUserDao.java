package org.marketcetera.persistence.hibernate;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.marketcetera.systemmodel.User;
import org.marketcetera.systemmodel.persistence.UserDao;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Repository
@ClassVersion("$Id$")
public class HibernateUserDao
        implements UserDao
{
    /**
     * Create a new HibernateUserDao instance.
     *
     * @param inSessionFactory
     */
    @Autowired
    public HibernateUserDao(SessionFactory inSessionFactory)
    {
        Validate.notNull(inSessionFactory,
                         "Session factory missing");
        sessionFactory = inSessionFactory;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.UserDao#getAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAll()
    {
        return currentSession().createQuery("from " + PersistentUser.class.getName()).list();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.UserDao#save(org.marketcetera.systemmodel.User)
     */
    @Override
    public void write(User inUser)
    {
        PersistentUser pUser;
        if(inUser instanceof PersistentUser) {
            pUser = (PersistentUser)inUser;
        } else {
            pUser = new PersistentUser(inUser);
        }
        currentSession().saveOrUpdate(pUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.UserDao#getByName(java.lang.String)
     */
    @Override
    public User getByName(String inUsername)
    {
        Criteria criteria = currentSession().createCriteria(PersistentUser.class);
        criteria.add(Restrictions.eq("name",
                                     inUsername));
        return (User)criteria.uniqueResult();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.UserDao#getById(long)
     */
    @Override
    public User getById(long inUserID)
    {
        Criteria criteria = currentSession().createCriteria(PersistentUser.class);
        criteria.add(Restrictions.eq("id",
                                     inUserID));
        return (User)criteria.uniqueResult();
    }
    /**
     *
     *
     *
     * @return
     */
    private Session currentSession()
    {
        return sessionFactory.getCurrentSession();
    }
    /**
     * 
     */
    private SessionFactory sessionFactory;
}
