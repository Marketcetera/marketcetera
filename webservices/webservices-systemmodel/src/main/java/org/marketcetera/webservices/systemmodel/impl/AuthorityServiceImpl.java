package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.AuthorityFactory;
import org.marketcetera.core.systemmodel.AuthorityManagerService;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.systemmodel.AuthorityService;
import org.marketcetera.webservices.systemmodel.WebServicesAuthority;

/* $License$ */

/**
 * Provides web-services access to the user service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AuthorityServiceImpl.java 16218 2012-08-27 23:23:59Z colin $
 * @since $Release$
 */
public class AuthorityServiceImpl
        implements AuthorityService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.AuthorityService#addAuthority(java.lang.String)
     */
    @Override
    public Response addAuthority(String inAuthority)
    {
        SLF4JLoggerProxy.trace(AuthorityServiceImpl.class,
                               "AuthorityService addAuthority invoked with authority {}", //$NON-NLS-1$
                               inAuthority);
        Response response;
        try {
            Authority authority = authorityFactory.create(inAuthority);
            authorityManagerService.addAuthority(authority);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(AuthorityServiceImpl.class,
                                  e);
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.AuthorityService#getAuthority(long)
     */
    @Override
    public WebServicesAuthority getAuthority(long inId)
    {
        SLF4JLoggerProxy.trace(AuthorityServiceImpl.class,
                               "AuthorityService getAuthority invoked with id {}", //$NON-NLS-1$
                               inId);
        Authority authority = authorityManagerService.getAuthorityById(inId);
        if(authority == null) {
            return null;
        }
        return new WebServicesAuthority(authority);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.AuthorityService#getAuthorities()
     */
    @Override
    public List<WebServicesAuthority> getAuthorities()
    {
        SLF4JLoggerProxy.trace(AuthorityServiceImpl.class,
                               "AuthorityService getAuthorities invoked"); //$NON-NLS-1$
        List<WebServicesAuthority> decoratedUsers = new ArrayList<WebServicesAuthority>();
        for(Authority authority : authorityManagerService.getAllAuthorities()) {
            decoratedUsers.add(new WebServicesAuthority(authority));
        }
        return decoratedUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.AuthorityService#deleteAuthority(long)
     */
    @Override
    public Response deleteAuthority(long inId)
    {
        SLF4JLoggerProxy.debug(AuthorityServiceImpl.class,
                               "AuthorityService deleteAuthority invoked with id {}", //$NON-NLS-1$
                               inId);
        Response response;
        try {
            Authority authority = authorityManagerService.getAuthorityById(inId);
            authorityManagerService.deleteAuthority(authority);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /**
     * Sets the authorityManagerService value.
     *
     * @param an <code>AuthorityManagerService</code> value
     */
    public void setAuthorityManagerService(AuthorityManagerService inAuthorityManagerService)
    {
        authorityManagerService = inAuthorityManagerService;
    }
    /**
     * Sets the authorityFactory value.
     *
     * @param an <code>AuthorityFactory</code> value
     */
    public void setAuthorityFactory(AuthorityFactory inAuthorityFactory)
    {
        authorityFactory = inAuthorityFactory;
    }
    /**
     * data access object
     */
    private AuthorityManagerService authorityManagerService;
    /**
     * constructs authority objects 
     */
    private AuthorityFactory authorityFactory;
}
