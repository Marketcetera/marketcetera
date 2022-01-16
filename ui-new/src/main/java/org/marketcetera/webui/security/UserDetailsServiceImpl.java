package org.marketcetera.webui.security;

import java.util.List;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.User;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides authentication and authorization services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class UserDetailsServiceImpl
        implements UserDetailsService
{
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String inUsername)
            throws UsernameNotFoundException
    {
        User user = userService.findByName(inUsername);
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + inUsername);
        } else {
            Set<Permission> permissions = authService.findAllPermissionsByUsername(inUsername);
            List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
            permissions.forEach(permission -> grantedAuthorities.add(permission));
            return new org.springframework.security.core.userdetails.User(user.getName(),
                                                                          user.getHashedPassword(),
                                                                          permissions);
        }
    }
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authService;
}
