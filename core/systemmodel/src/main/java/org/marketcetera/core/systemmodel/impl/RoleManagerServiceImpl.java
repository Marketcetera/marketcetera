package org.marketcetera.core.systemmodel.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.api.security.RoleManagerService;

/* $License$ */

/**
 * Provides Role manager services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RoleManagerServiceImpl
        implements RoleManagerService
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.RoleManagerService#getRoleByName(java.lang.String)
     */
    @Override
    public Role getRoleByName(String inName)
    {
        inName = StringUtils.trimToNull(inName);
        Validate.notNull(inName);
        return roleDao.getByName(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.RoleManagerService#getRoleById(long)
     */
    @Override
    public Role getRoleById(long inId)
    {
        return roleDao.getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.RoleManagerService#getAllRoles()
     */
    @Override
    public List<Role> getAllRoles()
    {
        return roleDao.getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.RoleManagerService#addRole(org.marketcetera.api.dao.Role)
     */
    @Override
    public void addRole(Role inRole)
    {
        Validate.notNull(inRole);
        roleDao.add(inRole);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.RoleManagerService#saveRole(org.marketcetera.api.dao.Role)
     */
    @Override
    public void saveRole(Role inRole)
    {
        Validate.notNull(inRole);
        roleDao.save(inRole);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.RoleManagerService#deleteRole(org.marketcetera.api.dao.Role)
     */
    @Override
    public void deleteRole(Role inRole)
    {
        Validate.notNull(inRole);
        roleDao.delete(inRole);
    }
    /**
     * Sets the roleDao value.
     *
     * @param inRoleDao <code>RoleDao</code> value
     */
    public void setRoleDao(RoleDao inRoleDao)
    {
        roleDao = inRoleDao;
    }
    /**
     * group DAO value
     */
    private RoleDao roleDao;
}
