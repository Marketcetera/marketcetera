package org.marketcetera.dao.impl;

import org.marketcetera.api.systemmodel.Role;
import org.marketcetera.dao.AbstractNamedDao;
import org.marketcetera.dao.RoleDao;
import org.marketcetera.dao.domain.PersistentRole;

/**
 * @version $Id$
 * @date 6/29/12 12:39 AM
 */

public class RoleDaoImpl
        extends AbstractNamedDao<Role>
        implements RoleDao
{
    /* (non-Javadoc)
     * @see org.marketcetera.dao.AbstractNamedDao#getByNameQuery()
     */
    @Override
    protected String getByNameQuery()
    {
        return "Role.findByName";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.AbstractDao#getCountQueryName()
     */
    @Override
    protected String getCountQueryName()
    {
        return "Role.count";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.AbstractDao#getAllQueryName()
     */
    @Override
    protected String getAllQueryName()
    {
        return "Role.findAll";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.dao.AbstractDao#getDataType()
     */
    @Override
    protected Class<? extends Role> getDataType()
    {
        return PersistentRole.class;
    }
}
