package org.marketcetera.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.marketcetera.dao.GroupDao;
import org.marketcetera.dao.impl.PersistentGroup;
import org.marketcetera.systemmodel.Group;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides Hibernate access to {@link Group} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Repository
@Transactional(readOnly=true)
@ClassVersion("$Id$")
public class HibernateGroupDao
        implements GroupDao
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.PersistentSystemObjectDao#add(org.marketcetera.systemmodel.SystemObject)
     */
    @Override
    @Transactional(readOnly=false)
    public void add(Group inData)
    {
        getCurrentSession().save(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.PersistentSystemObjectDao#getById(long)
     */
    @Override
    public PersistentGroup getById(long inId)
    {
        return (PersistentGroup)getCurrentSession().get(PersistentGroup.class,
                                                        inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.GroupDao#getByName(java.lang.String)
     */
    @Override
    public Group getByName(String inName)
    {
        Query query = getCurrentSession().getNamedQuery("findGroupByName").setString("name",
                                                                                     inName);
        List<?> results = query.list();
        // we fully expect there to be 0 or 1 group for the name
        if(results.size() > 1) {
            throw new IllegalArgumentException("Too many results"); // TODO
        }
        if(results.isEmpty()) {
            return null;
        }
        return (Group)results.get(0);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.PersistentSystemObjectDao#save(org.marketcetera.systemmodel.SystemObject)
     */
    @Override
    @Transactional(readOnly=false)
    public void save(Group inData)
    {
        getCurrentSession().update(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.GroupDao#delete(org.marketcetera.systemmodel.Group)
     */
    @Override
    @Transactional(readOnly=false)
    public void delete(Group inData)
    {
        getCurrentSession().delete(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.GroupDao#findAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Group> getAll()
    {
        Query query = getCurrentSession().getNamedQuery("findAllGroups");
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
