package org.marketcetera.dao;

import org.marketcetera.api.dao.NamedDao;
import org.marketcetera.api.systemmodel.NamedObject;

/* $License$ */

/**
 * Provides common behaviors for named object DAO implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractNamedDao<Clazz extends NamedObject>
        extends AbstractDao<Clazz>
        implements NamedDao<Clazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.NamedDao#getByName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Clazz getByName(String inName)
    {
        return (Clazz)getEntityManager().createNamedQuery(getByNameQuery()).getSingleResult();
    }
    /**
     * Gets the name of the <code>getByName</code> query.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getByNameQuery();
}
