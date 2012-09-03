package org.marketcetera.core.systemmodel.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.PermissionManagerService;

/* $License$ */

/**
 * Provides Permission manager services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PermissionManagerServiceImpl
        implements PermissionManagerService
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.PermissionManagerService#getPermissionByName(java.lang.String)
     */
    @Override
    public Permission getPermissionByName(String inName)
    {
        inName = StringUtils.trimToNull(inName);
        Validate.notNull(inName);
        return permissionDao.getByName(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.PermissionManagerService#getPermissionById(long)
     */
    @Override
    public Permission getPermissionById(long inId)
    {
        return permissionDao.getById(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.PermissionManagerService#getAllPermissions()
     */
    @Override
    public List<Permission> getAllPermissions()
    {
        return permissionDao.getAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.PermissionManagerService#addPermission(org.marketcetera.api.dao.Permission)
     */
    @Override
    public void addPermission(Permission inPermission)
    {
        Validate.notNull(inPermission);
        permissionDao.add(inPermission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.PermissionManagerService#savePermission(org.marketcetera.api.dao.Permission)
     */
    @Override
    public void savePermission(Permission inPermission)
    {
        Validate.notNull(inPermission);
        permissionDao.save(inPermission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.PermissionManagerService#deletePermission(org.marketcetera.api.dao.Permission)
     */
    @Override
    public void deletePermission(Permission inPermission)
    {
        Validate.notNull(inPermission);
        permissionDao.delete(inPermission);
    }
    /**
     * Sets the permissionDao value.
     *
     * @param inPermissionDao <code>PermissionDao</code> value
     */
    public void setPermissionDao(PermissionDao inPermissionDao)
    {
        permissionDao = inPermissionDao;
    }
    /**
     * permission DAO value
     */
    private PermissionDao permissionDao;
}
