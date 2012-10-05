package org.marketcetera.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.api.dao.SystemInformation;
import org.marketcetera.api.dao.SystemInformationDao;

/* $License$ */

/**
 * Provides datastore access to <code>SystemInformation</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SystemInformationDaoImpl
        implements SystemInformationDao
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.SystemInformationDao#get()
     */
    @Override
    public SystemInformation get()
    {
        return (SystemInformation)getEntityManager().createNamedQuery("PersistentSystemInformation.findAll").getSingleResult();
    }
    /**
     * Sets the entity manager value.
     *
     * @param inEntityManager an <code>EntityManager</code> value
     */
    @PersistenceContext
    public void setEntityManager(EntityManager inEntityManager)
    {
        entityManager = inEntityManager;
    }
    /**
     * Get the entityManager value.
     *
     * @return an <code>EntityManager</code> value
     */
    private EntityManager getEntityManager()
    {
        return entityManager;
    }
    /**
     * entity manager value
     */
    private EntityManager entityManager;
}
