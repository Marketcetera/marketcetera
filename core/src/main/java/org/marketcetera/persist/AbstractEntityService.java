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
@Transactional(propagation=Propagation.REQUIRED,readOnly=true,rollbackFor=RuntimeException.class)
public abstract class AbstractEntityService<Clazz extends EntityBase>
        implements EntityService<Clazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#update(org.marketcetera.persist.EntityBase)
     */
    @Override
    @Transactional(readOnly=false)
    public void update(Clazz inData)
    {
        dao.save(inData);
        doAfterUpdate(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#read(long)
     */
    @Override
    public Clazz read(long inId)
    {
        return dao.getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityService#readAll()
     */
    @Override
    public List<Clazz> readAll()
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
    protected void doAfterUpdate(Clazz inData) {}
    /**
     * provides datastore access to Clazz objects
     */
    @Autowired
    private DataAccessObject<Clazz> dao;
}
