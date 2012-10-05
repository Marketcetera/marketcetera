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
 * @version $Id$
 * @since $Release$
 */
public class PermissionServiceImpl
        implements PermissionService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermission(long)
     */
    @Override
    public WebServicesPermission getPermission(long inId)
    {
        Permission permission = permissionDao.getById(inId);
        if(permission == null) {
            return null;
        }
        return new WebServicesPermission(permission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#updatePermission(org.marketcetera.webservices.systemmodel.WebServicesPermission)
     */
    @Override
    public Response updatePermission(WebServicesPermission inPermission)
    {
        Response response;
        try {
            Permission persistablePermission = permissionFactory.create(inPermission);
            permissionDao.save(persistablePermission);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#addPermission(java.lang.String)
     */
    @Override
    public WebServicesPermission addPermission(WebServicesPermission inPermission)
    {
        try {
            Permission persistablePermission = permissionFactory.create(inPermission);
            permissionDao.add(persistablePermission);
            return new WebServicesPermission(persistablePermission);
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(PermissionServiceImpl.class,
                                  e);
            throw e;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermissions()
     */
    @Override
    public List<WebServicesPermission> getPermissions()
    {
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
    public Response deletePermission(final long inId)
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
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError()
                               .entity(e.getMessage()).build();
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
    /**
     * Sets the permission DAO value.
     *
     * @param permissionDao a <code>PermissionDao</code> value
     */
    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }
    /**
     * allows datastore access for permission objects
     */
    private PermissionDao permissionDao;
    /**
     * constructs permission objects
     */
    private PermissionFactory permissionFactory;
}
