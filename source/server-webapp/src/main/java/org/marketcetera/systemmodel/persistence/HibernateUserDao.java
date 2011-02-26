package org.marketcetera.systemmodel.persistence;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.marketcetera.systemmodel.User;
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
        return currentSession().createQuery("from " + User.class.getName()).list();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.UserDao#save(org.marketcetera.systemmodel.User)
     */
    @Override
    public void write(User inUser)
    {
        currentSession().saveOrUpdate(inUser);
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
