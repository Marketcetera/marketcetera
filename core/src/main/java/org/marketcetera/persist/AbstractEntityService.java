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
     * @see org.marketcetera.persist.EntityService#findAll()
     */
    @Override
    public List<Clazz> findAll()
    {
        return dao.getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#create(org.marketcetera.persist.EntityBase)
     */
    @Override
    @Transactional(readOnly=false)
    public void create(Clazz inData)
    {
        dao.add(inData);
    }
    @Autowired
    private DataAccessObject<Clazz> dao;
}
