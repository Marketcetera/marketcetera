package org.marketcetera.persist;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides common behaviors for entity service implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
public abstract class AbstractEntityService<Clazz extends EntityBase>
        implements EntityService<Clazz>
{
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().PagingAndSortinggetRepository()#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<Clazz> findAll(Pageable inPage)
    {
        return getRepository().findAll(inPage);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().PagingAndSortinggetRepository()#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    public Iterable<Clazz> findAll(Sort inSort)
    {
        return getRepository().findAll(inSort);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#count()
     */
    @Override
    public long count()
    {
        return getRepository().count();
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#delete(java.lang.Object)
     */
    @Override
    public void delete(Clazz inType)
    {
        getRepository().delete(inType);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#delete(java.lang.Iterable)
     */
    @Override
    public void delete(Iterable<? extends Clazz> inIterator)
    {
        getRepository().delete(inIterator);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#delete(java.io.Serializable)
     */
    @Override
    public void delete(Long inId)
    {
        getRepository().delete(inId);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#deleteAll()
     */
    @Override
    public void deleteAll()
    {
        getRepository().deleteAll();
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#exists(java.io.Serializable)
     */
    @Override
    public boolean exists(Long inId)
    {
        return getRepository().exists(inId);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#findAll()
     */
    @Override
    public Iterable<Clazz> findAll()
    {
        return getRepository().findAll();
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#findAll(java.lang.Iterable)
     */
    @Override
    public Iterable<Clazz> findAll(Iterable<Long> inIdSet)
    {
        return getRepository().findAll(inIdSet);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#findOne(java.io.Serializable)
     */
    @Override
    public Clazz findOne(Long inId)
    {
        Clazz value = getRepository().findOne(inId);
        if(value == null) {
            throw new EntityNotFoundException();
        }
        return value;
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#save(java.lang.Iterable)
     */
    @Override
    public <S extends Clazz> Iterable<S> save(Iterable<S> inDataSet)
    {
        return getRepository().save(inDataSet);
    }
    /* (non-Javadoc)
     * @see org.springframework.data.getRepository().CrudgetRepository()#save(java.lang.Object)
     */
    @Override
    public <S extends Clazz> S save(S inData)
    {
        return getRepository().save(inData);
    }
    /**
     * 
     *
     *
     * @return a <code>PagingAndSortinggetRepository()&lt;Clazz,Long&gt;</code> value
     */
    protected abstract PagingAndSortingRepository<Clazz,Long> getRepository();
}
