package org.marketcetera.webui.security;

import java.util.Optional;

import org.marketcetera.admin.User;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class AuthenticatedUser
{
    /**
     * 
     *
     *
     * @return
     */
    public Optional<User> get()
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext == null || securityContext.getAuthentication() instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        try {
            AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
            return Optional.ofNullable(adminClientService.getCurrentUser());
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  "Unable to return the current authenticated user");
            return Optional.empty();
        }
    }
    /**
     * 
     *
     *
     */
    public void logout()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        SLF4JLoggerProxy.info(this,
                              "Logging out {}",
                              context.getAuthentication().getName());
        // TODO service manager logout
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(),
                             null,
                             null);
    }
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
}
