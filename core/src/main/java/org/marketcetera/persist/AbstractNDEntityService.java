package org.marketcetera.persist;

import javax.persistence.EntityNotFoundException;

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
public abstract class AbstractNDEntityService<Clazz extends NDEntityBase>
        extends AbstractEntityService<Clazz>
        implements NDEntityService<Clazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.NDEntityRepository#findByName(java.lang.String)
     */
    @Override
    public Clazz findByName(String inName)
    {
        Clazz value = getRepository().findByName(inName);
        if(value == null) {
            throw new EntityNotFoundException();
        }
        return value;
    }
    protected NDEntityRepository<Clazz> getRepository()
    {
        return (NDEntityRepository<Clazz>)super.getRepository();
    }
    protected abstract Class<? extends NDEntityRepository<Clazz>> getRepositoryType();
}
