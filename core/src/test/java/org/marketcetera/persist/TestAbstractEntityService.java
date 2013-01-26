package org.marketcetera.persist;

import java.util.List;

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
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doBeforeCreate(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doBeforeCreate(Clazz inData)
    {
        if(beforeException != null) {
            throw beforeException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doAfterCreate(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doAfterCreate(Clazz inData)
    {
        if(afterException != null) {
            throw afterException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doBeforeRead(long)
     */
    @Override
    protected void doBeforeRead(long inId)
    {
        if(beforeException != null) {
            throw beforeException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doAfterRead(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doAfterRead(Clazz inData)
    {
        if(afterException != null) {
            throw afterException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doBeforeUpdate(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doBeforeUpdate(Clazz inData)
    {
        if(beforeException != null) {
            throw beforeException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doBeforeDelete(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doBeforeDelete(Clazz inData)
    {
        if(beforeException != null) {
            throw beforeException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doBeforeReadAll()
     */
    @Override
    protected void doBeforeReadAll()
    {
        if(beforeException != null) {
            throw beforeException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doAfterReadAll(java.util.List)
     */
    @Override
    protected void doAfterReadAll(List<Clazz> inData)
    {
        if(afterException != null) {
            throw afterException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doAfterUpdate(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doAfterUpdate(Clazz inData)
    {
        if(afterException != null) {
            throw afterException;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#doAfterDelete(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doAfterDelete(Clazz inData)
    {
        if(afterException != null) {
            throw afterException;
        }
    }
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
    private RuntimeException beforeException;
    private RuntimeException afterException;
}
