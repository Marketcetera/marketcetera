package org.marketcetera.persist;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TestEntityService<Clazz extends EntityBase>
        extends EntityService<Clazz>
{
    public void resetExceptions();
    public void setBeforeException(RuntimeException inException);
    public void setAfterException(RuntimeException inException);
}
