package org.marketcetera.persist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TestAbstractEntityService<Clazz extends EntityBase>
        extends AbstractEntityService<Clazz>
        implements TestEntityService<Clazz>
{
    public void resetExceptions()
    {
        beforeException = null;
        afterException = null;
    }
    public void setBeforeException(RuntimeException inException)
    {
        beforeException = inException;
    }
    public void setAfterException(RuntimeException inException)
    {
        afterException = inException;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<Clazz> findAll(Pageable inPage)
    {
        return super.findAll(inPage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    public Iterable<Clazz> findAll(Sort inSort)
    {
        return super.findAll(inSort);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#count()
     */
    @Override
    public long count()
    {
        return super.count();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#delete(org.marketcetera.persist.EntityBase)
     */
    @Override
    public void delete(Clazz inType)
    {
        if(beforeException != null) {
            throw beforeException;
        }
        super.delete(inType);
        if(afterException != null) {
            throw afterException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#delete(java.lang.Iterable)
     */
    @Override
    public void delete(Iterable<? extends Clazz> inIterator)
    {
        if(beforeException != null) {
            throw beforeException;
        }
        super.delete(inIterator);
        if(afterException != null) {
            throw afterException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#delete(java.lang.Long)
     */
    @Override
    public void delete(Long inId)
    {
        if(beforeException != null) {
            throw beforeException;
        }
        super.delete(inId);
        if(afterException != null) {
            throw afterException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#deleteAll()
     */
    @Override
    public void deleteAll()
    {
        super.deleteAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#exists(java.lang.Long)
     */
    @Override
    public boolean exists(Long inId)
    {
        return super.exists(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#findAll()
     */
    @Override
    public Iterable<Clazz> findAll()
    {
        return super.findAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#findAll(java.lang.Iterable)
     */
    @Override
    public Iterable<Clazz> findAll(Iterable<Long> inIdSet)
    {
        // TODO Auto-generated method stub
        return super.findAll(inIdSet);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#findOne(java.lang.Long)
     */
    @Override
    public Clazz findOne(Long inId)
    {
        // TODO Auto-generated method stub
        return super.findOne(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#save(java.lang.Iterable)
     */
    @Override
    public <S extends Clazz> Iterable<S> save(Iterable<S> inDataSet)
    {
        if(beforeException != null) {
            throw beforeException;
        }
        Iterable<S> result = super.save(inDataSet);
        if(afterException != null) {
            throw afterException;
        }
        return result;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#save(org.marketcetera.persist.EntityBase)
     */
    @Override
    public <S extends Clazz> S save(S inData)
    {
        if(beforeException != null) {
            throw beforeException;
        }
        S result = super.save(inData);
        if(afterException != null) {
            throw afterException;
        }
        return result;
    }
    private RuntimeException beforeException;
    private RuntimeException afterException;
}
