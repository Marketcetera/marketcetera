package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.marketcetera.api.dao.Group;
import org.marketcetera.core.systemmodel.GroupFactory;
import org.marketcetera.api.security.GroupManagerService;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.systemmodel.GroupService;
import org.marketcetera.webservices.systemmodel.WebServicesGroup;

/* $License$ */

/**
 * Provides web-services access to the group service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class GroupServiceImpl
        implements GroupService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.GroupService#addGroup(java.lang.String)
     */
    @Override
    public Response addGroup(String inName)
    {
        SLF4JLoggerProxy.trace(GroupServiceImpl.class,
                               "GroupService addGroup invoked with group {}", //$NON-NLS-1$
                               inName);
        Response response;
        try {
            Group group = groupFactory.create(inName);
            groupManagerService.addGroup(group);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(GroupServiceImpl.class,
                                  e);
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.GroupService#getGroup(long)
     */
    @Override
    public WebServicesGroup getGroup(long inId)
    {
        SLF4JLoggerProxy.trace(GroupServiceImpl.class,
                               "GroupService getGroup invoked with id {}", //$NON-NLS-1$
                               inId);
        Group group = groupManagerService.getGroupById(inId);
        if(group == null) {
            return null;
        }
        return new WebServicesGroup(group);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.GroupService#getGroups()
     */
    @Override
    public List<WebServicesGroup> getGroups()
    {
        SLF4JLoggerProxy.trace(GroupServiceImpl.class,
                               "GroupService getGroups invoked"); //$NON-NLS-1$
        List<WebServicesGroup> decoratedGroups = new ArrayList<WebServicesGroup>();
        for(Group group : groupManagerService.getAllGroups()) {
            decoratedGroups.add(new WebServicesGroup(group));
        }
        return decoratedGroups;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.GroupService#deleteGroup(long)
     */
    @Override
    public Response deleteGroup(long inId)
    {
        SLF4JLoggerProxy.debug(GroupServiceImpl.class,
                               "GroupService deleteGroup invoked with id {}", //$NON-NLS-1$
                               inId);
        Response response;
        try {
            Group group = groupManagerService.getGroupById(inId);
            groupManagerService.deleteGroup(group);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /**
     * Sets the groupManagerService value.
     *
     * @param an <code>GroupManagerService</code> value
     */
    public void setGroupManagerService(GroupManagerService inGroupManagerService)
    {
        groupManagerService = inGroupManagerService;
    }
    /**
     * Sets the groupFactory value.
     *
     * @param an <code>GroupFactory</code> value
     */
    public void setGroupFactory(GroupFactory inGroupFactory)
    {
        groupFactory = inGroupFactory;
    }
    /**
     * data access object
     */
    private GroupManagerService groupManagerService;
    /**
     * constructs group objects 
     */
    private GroupFactory groupFactory;
}
