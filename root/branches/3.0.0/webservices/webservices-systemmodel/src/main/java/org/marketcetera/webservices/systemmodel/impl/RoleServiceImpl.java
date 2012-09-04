package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.security.RoleManagerService;
import org.marketcetera.core.systemmodel.RoleFactory;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.systemmodel.RoleService;
import org.marketcetera.webservices.systemmodel.WebServicesRole;

/* $License$ */

/**
 * Provides web-services access to the group service.
 *
 * @version $Id$
 * @since $Release$
 */
public class RoleServiceImpl
        implements RoleService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#addRole(java.lang.String)
     */
    @Override
    public Response addRole(String inName)
    {
        SLF4JLoggerProxy.trace(RoleServiceImpl.class,
                               "RoleService addRole invoked with group {}", //$NON-NLS-1$
                               inName);
        Response response;
        try {
            Role group = roleFactory.create(inName);
            groupManagerService.addRole(group);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(RoleServiceImpl.class,
                                  e);
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRole(long)
     */
    @Override
    public WebServicesRole getRole(long inId)
    {
        SLF4JLoggerProxy.trace(RoleServiceImpl.class,
                               "RoleService getRole invoked with id {}", //$NON-NLS-1$
                               inId);
        Role group = groupManagerService.getRoleById(inId);
        if(group == null) {
            return null;
        }
        return new WebServicesRole(group);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRoles()
     */
    @Override
    public List<WebServicesRole> getRoles()
    {
        SLF4JLoggerProxy.trace(RoleServiceImpl.class,
                               "RoleService getRoles invoked"); //$NON-NLS-1$
        List<WebServicesRole> decoratedRoles = new ArrayList<WebServicesRole>();
        for(Role group : groupManagerService.getAllRoles()) {
            decoratedRoles.add(new WebServicesRole(group));
        }
        return decoratedRoles;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#deleteRole(long)
     */
    @Override
    public Response deleteRole(long inId)
    {
        SLF4JLoggerProxy.debug(RoleServiceImpl.class,
                               "RoleService deleteRole invoked with id {}", //$NON-NLS-1$
                               inId);
        Response response;
        try {
            Role group = groupManagerService.getRoleById(inId);
            groupManagerService.deleteRole(group);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /**
     * Sets the groupManagerService value.
     *
     * @param inRoleManagerService an <code>RoleManagerService</code> value
     */
    public void setRoleManagerService(RoleManagerService inRoleManagerService)
    {
        groupManagerService = inRoleManagerService;
    }
    /**
     * Sets the roleFactory value.
     *
     * @param inRoleFactory an <code>RoleFactory</code> value
     */
    public void setRoleFactory(RoleFactory inRoleFactory)
    {
        roleFactory = inRoleFactory;
    }
    /**
     * data access object
     */
    private RoleManagerService groupManagerService;
    /**
     * constructs group objects 
     */
    private RoleFactory roleFactory;
}
