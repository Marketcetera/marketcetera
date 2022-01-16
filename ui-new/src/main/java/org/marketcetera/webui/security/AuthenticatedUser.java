package org.marketcetera.webui.security;

import java.util.Optional;

import org.marketcetera.admin.User;
import org.marketcetera.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

@Component
public class AuthenticatedUser
{
    /**
     * Get the underlying <code>User</code> value.
     *
     * @return an <code>Optional&lt;User&gt;</code> value
     */
    public Optional<User> get()
    {
        UserDetails details = getAuthenticatedUser();
        if (details == null) {
            return Optional.empty();
        }
        User user = userService.findByName(details.getUsername());
        return Optional.ofNullable(user);
    }
    /**
     * Logs the current user out.
     */
    public void logout()
    {
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(),
                             null,
                             null);
    }
    /**
     * Get the user details for the authenticated user.
     *
     * @return a <code>UserDetails</code> value
     */
    private UserDetails getAuthenticatedUser()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails)context.getAuthentication().getPrincipal();
            return userDetails;
        }
        return null;
    }
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
}
