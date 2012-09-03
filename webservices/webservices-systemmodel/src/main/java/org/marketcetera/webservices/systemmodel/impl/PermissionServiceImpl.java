package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.PermissionManagerService;
import org.marketcetera.core.systemmodel.PermissionFactory;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.systemmodel.PermissionService;
import org.marketcetera.webservices.systemmodel.WebServicesPermission;

/* $License$ */

/**
 * Provides web-services access to the permission service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
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
            permissionManagerService.addPermission(permission);
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
        Permission permission = permissionManagerService.getPermissionById(inId);
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
        SLF4JLoggerProxy.trace(PermissionServiceImpl.class,
                               "PermissionService getPermissions invoked"); //$NON-NLS-1$
        List<WebServicesPermission> decoratedUsers = new ArrayList<WebServicesPermission>();
        for(Permission permission : permissionManagerService.getAllPermissions()) {
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
            Permission permission = permissionManagerService.getPermissionById(inId);
            permissionManagerService.deletePermission(permission);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /**
     * Sets the permissionManagerService value.
     *
     * @param an <code>PermissionManagerService</code> value
     */
    public void setPermissionManagerService(PermissionManagerService inPermissionManagerService)
    {
        permissionManagerService = inPermissionManagerService;
    }
    /**
     * Sets the permissionFactory value.
     *
     * @param an <code>PermissionFactory</code> value
     */
    public void setPermissionFactory(PermissionFactory inPermissionFactory)
    {
        permissionFactory = inPermissionFactory;
    }
    /**
     * data access object
     */
    private PermissionManagerService permissionManagerService;
    /**
     * constructs permission objects
     */
    private PermissionFactory permissionFactory;
}
