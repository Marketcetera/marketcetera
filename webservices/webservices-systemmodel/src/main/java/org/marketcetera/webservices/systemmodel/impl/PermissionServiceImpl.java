package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.api.dao.PermissionFactory;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.systemmodel.PermissionService;
import org.marketcetera.webservices.systemmodel.WebServicesPermission;

/* $License$ */

/**
 * Provides web-services access to the permission service.
 *
 * @version $Id: PermissionServiceImpl.java 16218 2012-08-27 23:23:59Z colin $
 * @since $Release$
 */
public class PermissionServiceImpl
        implements PermissionService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#addPermission(java.lang.String)
     */
    @Override
    public Response addPermission(String inPermission)
    {
        SLF4JLoggerProxy.trace(PermissionServiceImpl.class,
                               "PermissionService addPermission invoked with permission {}", //$NON-NLS-1$
                               inPermission);
        Response response;
        try {
            Permission permission = permissionFactory.create(inPermission);
            permissionDao.add(permission);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(PermissionServiceImpl.class,
                                  e);
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermission(long)
     */
    @Override
    public WebServicesPermission getPermission(long inId)
    {
        SLF4JLoggerProxy.trace(PermissionServiceImpl.class,
                               "PermissionService getPermission invoked with id {}", //$NON-NLS-1$
                               inId);
        Permission permission = permissionDao.getById(inId);
        if(permission == null) {
            return null;
        }
        return new WebServicesPermission(permission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermissions()
     */
    @Override
    public List<WebServicesPermission> getPermissions()
    {
        SLF4JLoggerProxy.trace(PermissionServiceImpl.class, "PermissionService getPermissions invoked"); //$NON-NLS-1$
        List<WebServicesPermission> decoratedUsers = new ArrayList<WebServicesPermission>();
        for(Permission permission : permissionDao.getAll()) {
            decoratedUsers.add(new WebServicesPermission(permission));
        }
        return decoratedUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#deletePermission(long)
     */
    @Override
    public Response deletePermission(long inId)
    {
        SLF4JLoggerProxy.debug(PermissionServiceImpl.class,
                               "PermissionService deletePermission invoked with id {}", //$NON-NLS-1$
                               inId);
        Response response;
        try {
            Permission permission = permissionDao.getById(inId);
            permissionDao.delete(permission);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /**
     * Sets the permissionFactory value.
     *
     * @param inPermissionFactory an <code>PermissionFactory</code> value
     */
    public void setPermissionFactory(PermissionFactory inPermissionFactory)
    {
        permissionFactory = inPermissionFactory;
    }

    private PermissionDao permissionDao;

    /**
     * constructs permission objects
     */
    private PermissionFactory permissionFactory;

    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }
}
