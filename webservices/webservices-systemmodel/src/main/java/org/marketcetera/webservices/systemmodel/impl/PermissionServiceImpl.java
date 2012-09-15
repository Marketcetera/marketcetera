package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

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
     * @see org.marketcetera.webservices.systemmodel.PermissionService#addPermission(java.lang.String)
     */
    @Override
    public WebServicesPermission addPermissionJSON(WebServicesPermission inPermission)
    {
        SLF4JLoggerProxy.debug(PermissionServiceImpl.class,
                               "PermissionService addPermissionJSON invoked with permission {}", //$NON-NLS-1$
                               inPermission);
        return doAddPermission(inPermission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermission(long)
     */
    @Override
    public WebServicesPermission getPermissionJSON(long inId)
    {
        SLF4JLoggerProxy.debug(PermissionServiceImpl.class,
                               "PermissionService getPermissionJSON invoked with id {}", //$NON-NLS-1$
                               inId);
        return new WebServicesPermission(doGetPermission(inId));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermissions()
     */
    @Override
    public List<WebServicesPermission> getPermissionsJSON()
    {
        SLF4JLoggerProxy.debug(PermissionServiceImpl.class, "PermissionService getPermissionsJSON invoked"); //$NON-NLS-1$
        return doGetPermissions();
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
            // TODO - imperfect, should use a transaction for this whole call
            if(permissionDao.isInUseByRole(inId)) {
                StatusType responseStatus = new StatusType() {
                    @Override
                    public Family getFamily()
                    {
                        return Family.SERVER_ERROR;
                    }
                    @Override
                    public String getReasonPhrase()
                    {
                        return "The permission with id " + inId + " is in use by a role and may not be deleted";
                    }
                    @Override
                    public int getStatusCode()
                    {
                        return Response.Status.PRECONDITION_FAILED.getStatusCode();
                    }
                };
                response = Response.serverError().status(responseStatus).build();
            } else {
                Permission permission = permissionDao.getById(inId);
                permissionDao.delete(permission);
                response = Response.ok().build();
            }
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError()
                               .entity(e.getMessage()).build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#addPermissionXML(org.marketcetera.api.dao.Permission)
     */
    @Override
    public WebServicesPermission addPermissionXML(WebServicesPermission inPermission)
    {
        return doAddPermission(inPermission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermissionXML(long)
     */
    @Override
    public WebServicesPermission getPermissionXML(long inId)
    {
        SLF4JLoggerProxy.debug(PermissionServiceImpl.class,
                               "PermissionService getPermissionXML invoked with id {}", //$NON-NLS-1$
                               inId);
        return doGetPermission(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getPermissionsXML()
     */
    @Override
    public List<WebServicesPermission> getPermissionsXML()
    {
        return doGetPermissions();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#updatePermissionJSON(org.marketcetera.webservices.systemmodel.WebServicesPermission)
     */
    @Override
    public Response updatePermissionJSON(WebServicesPermission inPermission)
    {
        return updatePermission(inPermission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#updatePermissionXML(org.marketcetera.webservices.systemmodel.WebServicesPermission)
     */
    @Override
    public Response updatePermissionXML(WebServicesPermission inPermission)
    {
        return updatePermission(inPermission);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.PermissionService#getAllByUserId(long)
     */
    @Override
    public List<WebServicesPermission> getAllByUserId(long inId)
    {
        List<WebServicesPermission> decoratedPermissions = new ArrayList<WebServicesPermission>();
        for(Permission permission : permissionDao.getAllByUserId(inId)) {
            decoratedPermissions.add(new WebServicesPermission(permission));
        }
        return decoratedPermissions;
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
     * Updates the given <code>Permission</code>.
     *
     * @param inPermission a <code>WebServicesPermission</code> value
     * @return a <code>Response</code> value
     */
    private Response updatePermission(WebServicesPermission inPermission)
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
    /**
     * Executes the retrieval of the the <code>Permission</code> object associated with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    private WebServicesPermission doGetPermission(long inId)
    {
        Permission permission = permissionDao.getById(inId);
        if(permission == null) {
            return null;
        }
        return new WebServicesPermission(permission);
    }
    /**
     * Executes the retrieval of all existing <code>Permission</code> objects.
     *
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    private List<WebServicesPermission> doGetPermissions()
    {
        List<WebServicesPermission> decoratedUsers = new ArrayList<WebServicesPermission>();
        for(Permission permission : permissionDao.getAll()) {
            decoratedUsers.add(new WebServicesPermission(permission));
        }
        return decoratedUsers;
    }
    /**
     * Executes the addition of the given <code>Permission</code> object.
     *
     * @param inPermission a <code>WebServicesPermission</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    private WebServicesPermission doAddPermission(WebServicesPermission inPermission)
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
    /**
     * allows datastore access for permission objects
     */
    private PermissionDao permissionDao;
    /**
     * constructs permission objects
     */
    private PermissionFactory permissionFactory;
}
