package org.marketcetera.webui.security;

import java.util.Optional;

import org.marketcetera.admin.User;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

@Component
public class AuthenticatedUser
{
    private Optional<Authentication> getAuthentication()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        System.out.println("COCO: AuthenticatedUser.getAuthentication using context: " + context);
        return Optional.ofNullable(context.getAuthentication()).filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken));
    }

    public Optional<User> get()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        System.out.println("COCO: AuthenticatedUser.get using context: " + context);
        if(context.getAuthentication() instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        try {
            AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
//        return getAuthentication().map(authentication -> userService.findByName(authentication.getName()));
        throw new UnsupportedOperationException("AuthenticatedUser.get() not implemented yet");
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
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(),
                             null,
                             null);
    }
    @Autowired
    private ServiceManager serviceManager;
}
