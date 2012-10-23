package org.marketcetera.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.marketcetera.api.dao.Dao;
import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 * Provides common DAO services.
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
     * @see org.marketcetera.api.dao.Dao#getAll(int, int, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Clazz> getAll(int inMaxResults,
                              int inFirstResult,
                              String inOrderBy)
    {
        Query query = entityManager.createNamedQuery(getAllQueryName());
        query.setFirstResult(inFirstResult);
        query.setMaxResults(inMaxResults);
        return query.getResultList();
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
    /**
     * Gets the name of the "get count" query. 
     *
     * @return a <code>String</code> value
     */
    protected abstract String getCountQueryName();
    /**
     * Gets the name of the "get all" query. 
     *
     * @return a <code>String</code> value
     */
    protected abstract String getAllQueryName();
    /**
     * Gets the type of the persistent object.
     *
     * @return a <code>Class&lt;? extends Clazz&gt;</code> value
     */
    protected abstract Class<? extends Clazz> getDataType();
    /**
     * entity manager value
     */
    private EntityManager entityManager;
}
