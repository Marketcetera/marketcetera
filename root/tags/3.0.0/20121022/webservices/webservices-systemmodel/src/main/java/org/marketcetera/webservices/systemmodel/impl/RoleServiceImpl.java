package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.api.dao.RoleFactory;
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
     * @see org.marketcetera.webservices.systemmodel.RoleService#addRole(org.marketcetera.webservices.systemmodel.WebServicesRole)
     */
    @Override
    public WebServicesRole add(WebServicesRole inRole)
    {
        try {
            Role persistableRole = roleFactory.create(inRole);
            roleDao.add(persistableRole);
            return new WebServicesRole(persistableRole);
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(RoleServiceImpl.class,
                                  e);
            throw e;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRole(long)
     */
    @Override
    public WebServicesRole get(long inId)
    {
        Role role = roleDao.getById(inId);
        if(role == null) {
            return null;
        }
        return new WebServicesRole(role);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRoles()
     */
    @Override
    public List<WebServicesRole> getAll()
    {
        List<WebServicesRole> decoratedRoles = new ArrayList<WebServicesRole>();
        for(Role role : roleDao.getAll()) {
            decoratedRoles.add(new WebServicesRole(role));
        }
        return decoratedRoles;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#updateRole(org.marketcetera.webservices.systemmodel.WebServicesRole)
     */
    @Override
    public Response update(WebServicesRole inRole)
    {
        Response response;
        try {
            Role persistableRole = roleFactory.create(inRole);
            roleDao.save(persistableRole);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#deleteRole(long)
     */
    @Override
    public Response delete(long inId)
    {
        SLF4JLoggerProxy.debug(RoleServiceImpl.class,
                               "RoleService deleteRole invoked with id {}", //$NON-NLS-1$
                               inId);
        Response response;
        try {
            Role role = roleDao.getById(inId);
            roleDao.delete(role);
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
     * Sets the roleFactory value.
     *
     * @param inRoleFactory an <code>RoleFactory</code> value
     */
    public void setRoleFactory(RoleFactory inRoleFactory)
    {
        roleFactory = inRoleFactory;
    }
    /**
     * Sets the <code>RoleDao</code> value.
     *
     * @param roleDao a <code>RoleDao</code> value
     */
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    /**
     * data access object
     */
    private RoleDao roleDao;
    /**
     * constructs group objects 
     */
    private RoleFactory roleFactory;
}
