package org.marketcetera.persist;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Transactional(propagation=Propagation.REQUIRED)
public abstract class AbstractEntityService<Clazz extends EntityBase>
        implements EntityService<Clazz>, ApplicationContextAware
{
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public final void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
        repository = applicationContext.getBean(getRepositoryType());
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    public Iterable<Clazz> findAll(Sort inSort)
    {
        return repository.findAll(inSort);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<Clazz> findAll(Pageable inPageable)
    {
        return repository.findAll(inPageable);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#count()
     */
    @Override
    public long count()
    {
        return repository.count();
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
     */
    @Override
    public void delete(Long inId)
    {
        repository.delete(inId);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Override
    public void delete(Clazz inType)
    {
        repository.delete(inType);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
     */
    @Override
    public void delete(Iterable<? extends Clazz> inType)
    {
        repository.delete(inType);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#deleteAll()
     */
    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
     */
    @Override
    public boolean exists(Long inArg0)
    {
        return repository.exists(inArg0);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    @Override
    public Iterable<Clazz> findAll()
    {
        return repository.findAll();
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(java.lang.Iterable)
     */
    @Override
    public Iterable<Clazz> findAll(Iterable<Long> inIterator)
    {
        return repository.findAll(inIterator);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
     */
    @Override
    public Clazz findOne(Long inId)
    {
        return repository.findOne(inId);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
     */
    @Override
    public <S extends Clazz> S save(S inData)
    {
        return repository.save(inData);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    @Override
    public <S extends Clazz> Iterable<S> save(Iterable<S> inDataIterator)
    {
        return repository.save(inDataIterator);
    }
    protected EntityRepository<Clazz> getRepository()
    {
        return repository;
    }
    protected abstract Class<? extends EntityRepository<Clazz>> getRepositoryType();
    /**
     * application context
     */
    private ApplicationContext applicationContext;
    private EntityRepository<Clazz> repository;
}
