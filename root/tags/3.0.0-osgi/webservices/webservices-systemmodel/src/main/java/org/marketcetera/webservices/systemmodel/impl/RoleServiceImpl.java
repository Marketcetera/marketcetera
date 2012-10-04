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
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRoles()
     */
    @Override
    public List<WebServicesRole> getRolesJSON()
    {
        return getRoles();
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
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#addRoleJSON(org.marketcetera.webservices.systemmodel.WebServicesRole)
     */
    @Override
    public WebServicesRole addRoleJSON(WebServicesRole inRole)
    {
        return addRole(inRole);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#addRoleXML(org.marketcetera.webservices.systemmodel.WebServicesRole)
     */
    @Override
    public WebServicesRole addRoleXML(WebServicesRole inRole)
    {
        return addRole(inRole);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRoleJSON(long)
     */
    @Override
    public WebServicesRole getRoleJSON(long inId)
    {
        return getRole(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRoleXML(long)
     */
    @Override
    public WebServicesRole getRoleXML(long inId)
    {
        return getRole(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#getRolesXML()
     */
    @Override
    public List<WebServicesRole> getRolesXML()
    {
        return getRoles();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#updateRoleJSON(org.marketcetera.webservices.systemmodel.WebServicesRole)
     */
    @Override
    public Response updateRoleJSON(WebServicesRole inRole)
    {
        return updateRole(inRole);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.RoleService#updateRoleXML(org.marketcetera.webservices.systemmodel.WebServicesRole)
     */
    @Override
    public Response updateRoleXML(WebServicesRole inRole)
    {
        return updateRole(inRole);
    }
    /**
     * Updates the given <code>Role</code>.
     *
     * @param inRole a <code>WebServicesRole</code> value
     * @return a <code>Response</code> value
     */
    private Response updateRole(WebServicesRole inRole)
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
    /**
     * Adds the given <code>WebServicesRole</code> to the datastore.
     *
     * @param inRole a <code>WebServicesRole</code> value
     * @return a <code>WebServicesRole</code> value
     */
    private WebServicesRole addRole(WebServicesRole inRole)
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
    /**
     * Executes the retrieval of all existing <code>Role</code> objects.
     *
     * @return a <code>List&lt;WebServicesRole&gt;</code> value
     */
    private List<WebServicesRole> getRoles()
    {
        List<WebServicesRole> decoratedRoles = new ArrayList<WebServicesRole>();
        for(Role role : roleDao.getAll()) {
            decoratedRoles.add(new WebServicesRole(role));
        }
        return decoratedRoles;
    }
    /**
     * Executes the retrieval of the the <code>Role</code> object associated with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>WebServicesRole</code> value
     */
    private WebServicesRole getRole(long inId)
    {
        Role role = roleDao.getById(inId);
        if(role == null) {
            return null;
        }
        return new WebServicesRole(role);
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
