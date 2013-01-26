package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Provides common behaviors for named object DAO implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractNamedDataAccessObject<Clazz extends NDEntityBase>
        extends AbstractDataAccessObject<Clazz>
        implements NDDataAccessObject<Clazz>
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.NamedDao#getByName(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Clazz getByName(String inName)
    {
        return (Clazz)getEntityManager().createNamedQuery(getByNameQuery()).getSingleResult();
    }
    /**
     * Gets the name of the <code>getByName</code> query.
     *
     * @return a <code>String</code> value
     */
    protected String getByNameQuery()
    {
        return getDataType().getSimpleName() + ".byName";
    }
}
