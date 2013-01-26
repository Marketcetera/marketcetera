package org.marketcetera.persist;

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
     * @see org.marketcetera.persist.AbstractEntityService#doAfterUpdate(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void doAfterUpdate(Clazz inData)
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
