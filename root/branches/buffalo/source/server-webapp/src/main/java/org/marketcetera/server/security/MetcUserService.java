package org.marketcetera.server.security;

import org.marketcetera.server.service.UserManager;
import org.marketcetera.systemmodel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetcUserService
        implements UserDetailsService
{
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String inUsername)
            throws UsernameNotFoundException, DataAccessException
    {
        System.out.println("Validating " + inUsername);
        User user = userManager.getByName(inUsername);
        if(user != null) {
            System.out.println("Found: " + user);
            return new MetcUserDetails(user);
        } else {
            System.out.println("Current users are: " + userManager.getUsers());
            user = new User();
            user.setActive(true);
            user.setDescription("test user");
            user.setHashedPassword(passwordManager.encodePassword(user,
                                                                  "password"));
            user.setName("user");
            userManager.write(user);
        }
        throw new UsernameNotFoundException(inUsername + " is not a valid user");
    }
    /**
     * password manager to use to get utilities for password management
     */
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    private UserManager userManager;
}
