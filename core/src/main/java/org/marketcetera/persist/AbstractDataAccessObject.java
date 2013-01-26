package org.marketcetera.persist;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides common DAO services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Transactional(propagation=Propagation.MANDATORY) // set transaction to MANDATORY to require the use of a business-level, wrapping service
@ClassVersion("$Id$")
public abstract class AbstractDataAccessObject<Clazz extends EntityBase>
        implements DataAccessObject<Clazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#getById(long)
     */
    @Override
    public Clazz getById(long inId)
    {
        Clazz returnValue = entityManager.find(getDataType(),
                                               inId);
        SLF4JLoggerProxy.trace(this,
                               "getById({}) found {}",
                               inId,
                               returnValue);
        if(returnValue == null) {
            throw new EntityNotFoundException();
        }
        return returnValue;
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
        SLF4JLoggerProxy.trace(this,
                               "Saving entity: {}",
                               inData);
        if(!entityManager.contains(inData)) {
            inData = entityManager.merge(inData);
        }
        entityManager.persist(inData);
//        entityManager.flush(); // TODO this is not correct because we want the wrapping service transaction to be able to rollback
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#add(java.lang.Object)
     */
    @Override
    public void add(Clazz inData)
    {
        SLF4JLoggerProxy.trace(this,
                               "Adding new entity: {}",
                               inData);
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
    protected String getCountQueryName()
    {
        return getDataType().getSimpleName() + ".count";
    }
    /**
     * Gets the name of the "get all" query. 
     *
     * @return a <code>String</code> value
     */
    protected String getAllQueryName()
    {
        return getDataType().getSimpleName() + ".findAll";
    }
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
