package org.marketcetera.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.api.dao.Dao;
import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractDao<Clazz extends SystemObject>
        implements Dao<Clazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#getById(long)
     */
    @Override
    public Clazz getById(long inId)
    {
        return entityManager.find(getDataType(),
                                  inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#getAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Clazz> getAll()
    {
        return entityManager.createNamedQuery(getAllQueryName()).getResultList();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#save(java.lang.Object)
     */
    @Override
    public void save(Clazz inData)
    {
        entityManager.merge(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#add(java.lang.Object)
     */
    @Override
    public void add(Clazz inData)
    {
        entityManager.persist(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#delete(java.lang.Object)
     */
    @Override
    public void delete(Clazz inData)
    {
        entityManager.remove(entityManager.merge(inData));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#getCount()
     */
    @Override
    public int getCount()
    {
        return ((Long)(entityManager.createNamedQuery(getCountQueryName()).getSingleResult())).intValue();
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
    protected EntityManager getEntityManager()
    {
        return entityManager;
    }
    protected abstract String getCountQueryName();
    protected abstract String getAllQueryName();
    protected abstract Class<? extends Clazz> getDataType();
    /**
     * entity manager value
     */
    private EntityManager entityManager;
}
