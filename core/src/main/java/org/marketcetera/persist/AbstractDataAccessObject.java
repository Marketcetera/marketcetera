package org.marketcetera.persist;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

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
@NotThreadSafe
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
     * @see org.marketcetera.persist.DataAccessObject#getAll(java.util.List)
     */
    @Override
    public List<Clazz> getAll(List<Order> inOrderBy)
    {
        CriteriaQuery<Clazz> query = getFindAllQuery();
        if(inOrderBy != null &&
           !inOrderBy.isEmpty()) {
            query.orderBy(inOrderBy);
        }
        return entityManager.createQuery(query).getResultList();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#getAll()
     */
    @Override
    public List<Clazz> getAll()
    {
        CriteriaQuery<Clazz> query = getFindAllQuery();
        List<Order> defaultOrderBy = getDefaultOrderBy();
        if(defaultOrderBy != null &&
           !defaultOrderBy.isEmpty()) {
            query.orderBy(defaultOrderBy);
        }
        return entityManager.createQuery(query).getResultList();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#getAll(int, int, java.util.List<javax.persistence.criteria.Order>)
     */
    @Override
    public List<Clazz> getAll(int inMaxResults,
                              int inFirstResult,
                              List<Order> inOrderBy)
    {
        CriteriaQuery<Clazz> query = getFindAllQuery();
        if(inOrderBy != null &&
           !inOrderBy.isEmpty()) {
            query.orderBy(inOrderBy);
        }
        return entityManager.createQuery(query).setFirstResult(inFirstResult).setMaxResults(inMaxResults).getResultList();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#save(java.lang.Object)
     */
    @Override
    public Clazz persist(Clazz inData)
    {
        SLF4JLoggerProxy.trace(this,
                               "Persisting entity: {}",
                               inData);
        if(!entityManager.contains(inData)) {
            inData = entityManager.merge(inData);
        }
        entityManager.persist(inData);
        return inData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Dao#delete(java.lang.Object)
     */
    @Override
    public void remove(Clazz inData)
    {
        if(!entityManager.contains(inData)) {
            inData = entityManager.merge(inData);
        }
        SLF4JLoggerProxy.trace(this,
                               "Removing entity: {}",
                               inData);
        entityManager.remove(inData);
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
     * Constructs a JPA query to retrieve all objects of this type from the database.
     * 
     * <p>Subclasses may override this method to return a specialized version of this query.
     *
     * @return a <code>CriteriaQuery&lt;Clazz&gt;</code> value
     */
    protected CriteriaQuery<Clazz> getFindAllQuery()
    {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Class<Clazz> dataType = getDataType();
        CriteriaQuery<Clazz> query = builder.createQuery(dataType);
        Root<Clazz> from = query.from(dataType);
        return query.select(from);
    }
    /**
     * Gets the default order-by clause to use for multiple-result queries for this type.
     * 
     * <p>The default behavior is to order the objects by id. Subclasses may override this
     * method to provide a difference behavior.
     *
     * @return a <code>List&lt;Order&gt;</code> value
     */
    protected List<Order> getDefaultOrderBy()
    {
        synchronized(defaultOrderBy) {
            if(defaultOrderBy.isEmpty()) {
                CriteriaBuilder builder = entityManager.getCriteriaBuilder();
                Class<Clazz> dataType = getDataType();
                CriteriaQuery<Clazz> query = builder.createQuery(dataType);
                Root<Clazz> from = query.from(dataType);
                // TODO refactor to allow special order-by
                /*
CriteriaQuery<Object> select = criteriaQuery.select(from);
        select.orderBy(criteriaBuilder.asc(from.get("pbyte"))
                        ,criteriaBuilder.desc(from.get("pint")));
                 */
            }
        }
        return defaultOrderBy;
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
     * @return a <code>Class&lt;Clazz&gt;</code> value
     */
    protected abstract Class<Clazz> getDataType();
    /**
     * entity manager value
     */
    private EntityManager entityManager;
    private static final List<Order> defaultOrderBy = new ArrayList<Order>();
}
