package org.marketcetera.webui.app.security;

import java.util.Collections;

import org.marketcetera.admin.User;
import org.marketcetera.admin.impl.SimpleUser;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Implements the {@link UserDetailsService}.
 * 
 * This implementation searches for {@link User} entities by the e-mail address
 * supplied in the login screen.
 */
@Service
@Primary
public class UserDetailsServiceImpl
        implements UserDetailsService
{
    /**
     * Recovers the {@link User} from the database using the e-mail address supplied
     * in the login screen. If the user is found, returns a
     * {@link org.springframework.security.core.userdetails.User}.
     *
     * @param username User's e-mail address
     * 
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException
    {
        // TODO
        SimpleUser user = new SimpleUser();
        user.setHashedPassword("password");
        user.setName(username);
        if (null == user) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getName(),
                                                                          user.getHashedPassword(),
                                                                          Collections.emptyList());//Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
        }
    }
}
