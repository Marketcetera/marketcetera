package org.marketcetera.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.marketcetera.systemmodel.SystemAuthority;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/* $License$ */

/**
 * Performs a set of initialization tasks with temporarily increased priviledges.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PriviledgedInitializer
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        try {
            // set up temporary authentication object
            Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            // add admin authority
            authorities.add(SystemAuthority.ROLE_ADMIN.getAsGrantedAuthority());
            // add user authority
            authorities.add(SystemAuthority.ROLE_USER.getAsGrantedAuthority());
            // perform authentication
            Authentication tempAuthentication = new PreAuthenticatedAuthenticationToken("bootstrapping-admin",
                                                                                        "bootstrapping-admin-password",
                                                                                        authorities);
            tempAuthentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(tempAuthentication);
            if(initializers != null) {
                for(Initializer initializer : initializers) {
                    try {
                        initializer.initialize();
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(UserInitializer.class,
                                              e,
                                              "Could not initialize, skipping"); // TODO
                    }
                }
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(PriviledgedInitializer.class,
                                  e,
                                  "Could not execute priviledged initialization, quitting"); // TODO
            return;
        } finally {
            // remove temp authentication
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }
    /**
     * Get the initializers value.
     *
     * @return a <code>List&lt;Initializer&gt;</code> value
     */
    public List<Initializer> getInitializers()
    {
        return initializers;
    }
    /**
     * Lists the initializers value.
     *
     * @param a <code>List&lt;Initializer&gt;</code> value
     */
    public void setInitializers(List<Initializer> inInitializers)
    {
        initializers = inInitializers;
    }
    /**
     * initializers to be executed
     */
    private List<Initializer> initializers;
}
