package org.marketcetera.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.marketcetera.dao.AuthorityDao;
import org.marketcetera.dao.impl.PersistentAuthority;
import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.api.attributes.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides Hibernate data access for {@link Authority} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HibernateAuthorityDao.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@Repository
@Transactional(readOnly=true)
public class HibernateAuthorityDao
        implements AuthorityDao
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.PersistentSystemObjectDao#add(org.marketcetera.core.systemmodel.SystemObject)
     */
    @Override
    @Transactional(readOnly=false)
    public void add(Authority inData)
    {
        getCurrentSession().save(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.PersistentSystemObjectDao#getById(long)
     */
    @Override
    public PersistentAuthority getById(long inId)
    {
        return (PersistentAuthority)getCurrentSession().get(PersistentAuthority.class,
                                                            inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.PersistentSystemObjectDao#save(org.marketcetera.core.systemmodel.SystemObject)
     */
    @Override
    @Transactional(readOnly=false)
    public void save(Authority inData)
    {
        getCurrentSession().update(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.AuthorityDao#getByName(java.lang.String)
     */
    @Override
    public Authority getByName(String inName)
    {
        Query query = getCurrentSession().getNamedQuery("findUserByName").setString("name",
                                                                                    inName);
        List<?> results = query.list();
        // we fully expect there to be 0 or 1 user for the name
        if(results.size() > 1) {
            throw new IllegalArgumentException("Too many results"); // TODO
        }
        if(results.isEmpty()) {
            return null;
        }
        return (Authority)results.get(0);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.AuthorityDao#getAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Authority> getAll()
    {
        Query query = getCurrentSession().getNamedQuery("findAllAuthorities");
        return query.list();
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
