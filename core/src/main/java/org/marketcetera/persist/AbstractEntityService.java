package org.marketcetera.persist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
public abstract class AbstractEntityService<Clazz extends EntityBase>
        implements EntityService<Clazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#delete(org.marketcetera.persist.EntityBase)
     */
    @Override
    @Transactional(readOnly=false)
    public void delete(Clazz inData)
    {
        doBeforeDelete(inData);
        dao.remove(inData);
        doAfterDelete(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#update(org.marketcetera.persist.EntityBase)
     */
    @Override
    @Transactional(readOnly=false)
    public Clazz update(Clazz inData)
    {
        doBeforeUpdate(inData);
        inData = dao.persist(inData);
        doAfterUpdate(inData);
        return inData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#read(long)
     */
    @Override
    public Clazz read(long inId)
    {
        doBeforeRead(inId);
        Clazz value = dao.getById(inId);
        doAfterRead(value);
        return value;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#readAll()
     */
    @Override
    public List<Clazz> readAll()
    {
        doBeforeReadAll();
        List<Clazz> values = dao.getAll();
        doAfterReadAll(values);
        return values;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#create(org.marketcetera.persist.EntityBase)
     */
    @Override
    @Transactional(readOnly=false)
    public Clazz create(Clazz inData)
    {
        doBeforeCreate(inData);
        Clazz value = dao.persist(inData);
        doAfterCreate(value);
        return value;
    }
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doBeforeCreate(Clazz inData) {}
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doAfterCreate(Clazz inData) {}
    /**
     * 
     *
     *
     * @param inId
     */
    protected void doBeforeRead(long inId) {}
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doAfterRead(Clazz inData) {}
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doBeforeUpdate(Clazz inData) {}
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doAfterUpdate(Clazz inData) {}
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doBeforeDelete(Clazz inData) {}
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doAfterDelete(Clazz inData) {}
    /**
     * 
     *
     *
     */
    protected void doBeforeReadAll() {}
    /**
     * 
     *
     *
     * @param inData
     */
    protected void doAfterReadAll(List<Clazz> inData) {}
    /**
     * provides datastore access to Clazz objects
     */
    @Autowired
    private DataAccessObject<Clazz> dao;
}
